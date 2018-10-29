package com.nixsolutions.logging.parameters.loggabletype.util;

import static com.nixsolutions.logging.LoggingConstants.SINGLE_PROPERTY;
import static com.nixsolutions.logging.common.MapUtils.mergeMaps;
import static com.nixsolutions.logging.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.getClassesToExtract;
import static com.nixsolutions.logging.parameters.loggabletype.util.AnnotatedTypeReflectionUtils
    .getRenamedFieldNameOrDefault;
import static com.nixsolutions.logging.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.getSupplierMethod;
import static com.nixsolutions.logging.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.isRecursiveLoop;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractorFactory;
import com.nixsolutions.logging.parameters.loggabletype.AnnotatedObject;
import com.nixsolutions.logging.parameters.loggabletype.ContextParamsAccessor;
import com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy;
import com.nixsolutions.logging.parameters.loggabletype.LookupResult;
import com.nixsolutions.logging.parameters.loggabletype.exception.LookupConflictException;
import com.nixsolutions.logging.parameters.loggabletype.exception.RecursiveLookupException;
import com.nixsolutions.logging.parameters.loggabletype.exception.RepeatedFieldsException;
import com.nixsolutions.logging.parameters.loggabletype.exception.UnresolvedLookupException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.nixsolutions.logging.common.MapUtils;

@Component
@SuppressWarnings("unchecked")
public class AnnotationReflectionLookupUtils implements AnnotationLookupConstants
{

  @Autowired
  private ContextParamExtractorFactory contextParamExtractorFactory;

  private LookupResult strategyLookupForField(Map<Class, List<Class>> fieldsProcessedBefore,
                                              Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair)
  {
    if (isRecursiveLoop(fieldsProcessedBefore, fieldObjPair.getLeft()))
    {
      return THROW_EX_LOOKUP.apply(new RecursiveLookupException());
    }

    AnnotatedObject<LoggableType> annotatedObject = fieldObjPair.getRight();
    ExtractionResolutionStrategy strategy = Optional.ofNullable(annotatedObject)
        .map(AnnotatedObject::getAnnotation)
        .map(LoggableType::resolutionStrategy)
        .orElse(ExtractionResolutionStrategy.COLLECTOR_FIRST);

    LookupResult collectorLookup = LookupResult.lazy(() -> objectCollectorLookup(fieldsProcessedBefore,
        fieldObjPair));
    LookupResult extractorLookup = LookupResult.lazy(() -> extractorLookup(annotatedObject));

    if (strategy == ExtractionResolutionStrategy.COLLECTOR_FIRST)
    {
      return LookupUtils.resultingLookup(
          collectorLookup,
          extractorLookup,
          DO_NOTHING_LOOKUP);
    }
    else if (strategy == ExtractionResolutionStrategy.EXTRACTOR_FIRST)
    {
      return LookupUtils.resultingLookup(
          extractorLookup,
          collectorLookup,
          DO_NOTHING_LOOKUP);
    }
    else if (strategy == ExtractionResolutionStrategy.RAISE_EX_ON_CONFLICT)
    {
      return LookupUtils.resultingLookup(LookupUtils.conflictingLookup(
          THROW_EX_LOOKUP.apply(new LookupConflictException()),
          collectorLookup,
          extractorLookup));
    }
    else //strategy = DO_NOTHING
    {
      return LookupUtils.resultingLookup(LookupUtils.conflictingLookup(
          DO_NOTHING_LOOKUP,
          collectorLookup,
          extractorLookup));
    }
  }

  public LookupResult strategyLookupForRootObj(AnnotatedObject<LoggableType> annotatedObject)
  {
    if (IS_TO_STRING_APPLICABLE_TO_CLASS.test(annotatedObject.getObjectClass()))
    {
      return LookupResult.createResolved(
          () -> Collections.singletonMap(SINGLE_PROPERTY, annotatedObject.getObject()));
    }

    return LookupResult.lazy(() -> strategyLookupForField(new HashMap<>(), Pair.of(null, annotatedObject)));
  }

  private LookupResult extractorLookup(AnnotatedObject<LoggableType> annotatedObject)
  {
    ContextParamExtractor extractor = contextParamExtractorFactory
        .getExtractorByClass(annotatedObject.getObjectClass());
    if (isNull(extractor))
    {
      return LookupResult.createUnresolved();
    }

    return LookupResult.createResolved(extractor::extractParams, annotatedObject.getObject());
  }

  private LookupResult objectCollectorLookup(Map<Class, List<Class>> fieldsProcessedBefore, Pair<Field,
      AnnotatedObject<LoggableType>> fieldObjPair)
  {
    AnnotatedObject<LoggableType> annotatedObject = fieldObjPair.getRight();

    return LookupUtils.resultingLookup(
        accessorMethodLookup(annotatedObject),
        annotatedMethodLookup(annotatedObject),
        collectorLookup(fieldsProcessedBefore, fieldObjPair));
  }

  private LookupResult collectorLookup(Map<Class, List<Class>> fieldsProcessedBefore, Pair<Field,
      AnnotatedObject<LoggableType>> fieldObjPair)
  {
    List<Pair<Field, AnnotatedObject<LoggableType>>> allFields = getAnnotatedFieldObjPairs(fieldObjPair.getRight());
    LookupResult eligibleFieldsContextParamLookup = getCompositeFieldsContextParamLookup(fieldsProcessedBefore,
        allFields);
    LookupResult notEligibleFieldsContextParamLookup = plainFieldsContextParamLookup(allFields);

    LookupResult errorLookup = LookupUtils.errorLookup(notEligibleFieldsContextParamLookup,
        eligibleFieldsContextParamLookup);

    if (errorLookup != null)
    {
      return errorLookup;
    }

    Map<String, Object> simpleContextParams = notEligibleFieldsContextParamLookup.executeForResult();
    Map<String, Object> complexContextParams = eligibleFieldsContextParamLookup.executeForResult();

    return LookupResult.createResolved(() ->
        MapUtils.mergeMaps(simpleContextParams, complexContextParams));
  }

  private Map<String, Object> mergeContextParamMaps(Map<String, Object> simpleContextParams,
                                                    Map<String, Object> complexContextParams,
                                                    Field complexField)
  {
    if (isNull(complexField))
    {
      return MapUtils.mergeMaps(simpleContextParams, complexContextParams);
    }

    Map<String, Object> finalResult = ImmutableMap.<String, Object>builder()
        .putAll(simpleContextParams)
        .put(complexField.getName(), complexContextParams).build();

    return finalResult;
  }

  //TODO CLEANUP/REFACTOR
  private LookupResult getCompositeFieldsContextParamLookup(Map<Class, List<Class>> fieldsProcessedBefore,
                                                            List<Pair<Field, AnnotatedObject<LoggableType>>> allFields)
  {
    List<Pair<Field, AnnotatedObject<LoggableType>>> compositeFields = allFields.stream()
        .filter(IS_FIELD_COMPLEX)
        .collect(toList());

    rejectErrorOnDuplicatingFields(compositeFields);

    if (compositeFields.isEmpty())
    {
      return DO_NOTHING_LOOKUP;
    }

    fieldsProcessedBefore.putAll(getClassFieldRelationMetadata(compositeFields));

    List<LookupResult> compositeFieldsLookups = new ArrayList<>();

    for (Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair : compositeFields)
    {
      LookupResult lookupResultToCheck =
          strategyLookupForField(fieldsProcessedBefore, fieldObjPair);

      if (lookupResultToCheck.isExceptional())
      {
        return lookupResultToCheck;
      }

      LookupResult adjustedToFieldResult = LookupResult.createResolved(() -> ImmutableMap.of(
          getRenamedFieldNameOrDefault(fieldObjPair.getLeft()),
          lookupResultToCheck.executeForResult()));
      compositeFieldsLookups.add(adjustedToFieldResult);
    }

    return LookupResult.lazy(() -> getMergedLookupResult(compositeFieldsLookups));
  }

  private Pair<LookupResult, AnnotatedObject<LoggableType>> toLookupObjFromCompositeFieldObjPair(
      Map<Class, List<Class>> fieldsProcessedBefore,
      Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair)
  {
    return Pair.of(strategyLookupForField(fieldsProcessedBefore, fieldObjPair), fieldObjPair.getRight());
  }

  private LookupResult getMergedLookupResult(List<LookupResult> compositeFields)
  {
    return LookupResult.createResolved(
        () -> compositeFields.stream()
            .map(LookupResult::executeForResult)
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .collect(toMap(Entry::getKey, Entry::getValue)));
  }

  private Map<Class, List<Class>> getClassFieldRelationMetadata(List<Pair<Field, AnnotatedObject<LoggableType>>>
                                                                    compositeFields)
  {
    return compositeFields.stream()
        .map(Pair::getLeft)
        .map(TO_PROCESSED_FIELDS_METADATA)
        .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList())));
  }

  private List<Pair<Field, AnnotatedObject<LoggableType>>> getAnnotatedFieldObjPairs(AnnotatedObject<LoggableType>
                                                                                         annotatedObject)
  {
    Function<Field, Pair<Field, AnnotatedObject<LoggableType>>> transformFn =
        FIELD_TO_FIELD_OBJ_CURRIED.apply(annotatedObject.getObject());
    List<Class> classes = getClassesToExtract(annotatedObject);
    return classes.stream()
        .map(Class::getDeclaredFields)
        .flatMap(Arrays::stream)
        .filter(field -> field.isAnnotationPresent(LoggableType.property.class))
        .map(transformFn)
        .collect(toList());
  }

  private LookupResult plainFieldsContextParamLookup(List<Pair<Field, AnnotatedObject<LoggableType>>> allFields)
  {
    try
    {
      Map<String, Object> contextParamsForNotEligibleFields = collectContextParamsForPlainFields(allFields);
      return LookupResult.createResolved(() -> contextParamsForNotEligibleFields);
    } catch (Exception e)
    {
      return LookupResult.createExceptional(() -> e);
    }
  }

  private Map<String, Object> collectContextParamsForPlainFields(List<Pair<Field, AnnotatedObject<LoggableType>>>
                                                                     allFields)
  {
    List<Pair<Field, AnnotatedObject<LoggableType>>> plainFields = allFields.stream()
        .filter(IS_FIELD_COMPLEX.negate())
        .collect(toList());

    rejectErrorOnDuplicatingFields(plainFields);

    return plainFields.stream()
        .flatMap(this::toFieldNameValuePair)
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  private void rejectErrorOnDuplicatingFields(List<Pair<Field, AnnotatedObject<LoggableType>>> fields)
  {
    Multimap<String, String> fieldClassesCollision = MultimapBuilder
        .hashKeys()
        .arrayListValues()
        .build();

    fields.stream()
        .map(pair -> pair.getKey())
        .forEach(field ->
            fieldClassesCollision.put(
                getRenamedFieldNameOrDefault(field),
                field.getDeclaringClass().getName()));

    Map<String, Collection<String>> repeatedFields = fieldClassesCollision.asMap().entrySet().stream()
        .filter(entry -> entry.getValue().size() > 1)
        .collect(toMap(Entry::getKey, Entry::getValue));

    if (repeatedFields.size() >= 1)
    {
      throw new RepeatedFieldsException(repeatedFields.toString());
    }

  }

  private Stream<Entry<String, Object>> toFieldNameValuePair(Pair<Field, AnnotatedObject<LoggableType>> fieldObjectPair)
  {
    Field field = fieldObjectPair.getLeft();
    Object value = fieldObjectPair.getRight().getObject();
    String fieldName = getRenamedFieldNameOrDefault(field);

    if (IS_TO_STRING_APPLICABLE_TO_CLASS.test(field.getType()))
    {
      return Stream.of(Pair.of(fieldName, value));
    }

    LookupResult lookupResult = extractorLookup(fieldObjectPair.getRight());
    if (lookupResult.isResolved())
    {
      return lookupResult.executeForResult().entrySet().stream();
    }

    throw new UnresolvedLookupException(format(FIELD_NON_EXTRACTABLE_EXCEPTION_MESSAGE, fieldName));
  }

  private LookupResult accessorMethodLookup(AnnotatedObject<LoggableType> annotatedObject)
  {
    Object object = annotatedObject.getObject();
    if (object instanceof ContextParamsAccessor)
    {
      return LookupResult.createResolved(((ContextParamsAccessor) object)::extractParams);
    }
    return LookupResult.createUnresolved();
  }

  private LookupResult annotatedMethodLookup(AnnotatedObject<LoggableType> annotatedObject)
  {
    try
    {
      Object object = annotatedObject.getObject();
      Optional<Method> supplierMethod = getSupplierMethod(object);
      if (supplierMethod.isPresent())
      {
        Map<String, Object> invocationResult = (Map<String, Object>) supplierMethod.get().invoke(object);
        return LookupResult.createResolved(() -> invocationResult);
      }
    } catch (Exception ex)
    {
      return LookupResult.createExceptional(() -> ex);
    }
    return LookupResult.createUnresolved();
  }

}