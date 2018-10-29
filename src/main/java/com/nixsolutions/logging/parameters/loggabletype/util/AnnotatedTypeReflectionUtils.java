package com.nixsolutions.logging.parameters.loggabletype.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.AnnotatedObject;

public class AnnotatedTypeReflectionUtils
{
  public static List<Class> getClassesToExtract(AnnotatedObject<LoggableType> annotatedObject)
  {
    LoggableType annotation = annotatedObject.getAnnotation();
    if (isNull(annotation))
    {
      return Collections.emptyList();
    }


    Class objectClass = annotatedObject.getObjectClass();
    return (annotation.ignoreParents()) ? Collections.singletonList(objectClass) : getClassesHierarchy(objectClass);
  }

  public static List<Class> getClassesHierarchy(Class clazz)
  {
    List<Class> classList = new ArrayList<>();
    classList.add(clazz);
    Class superclass = clazz.getSuperclass();
    classList.add(superclass);
    while (superclass != Object.class)
    {
      clazz = superclass;
      superclass = clazz.getSuperclass();
      if (!superclass.isInterface() && superclass.isAnnotationPresent(LoggableType.class))
      {
        classList.add(superclass);
      }
    }
    Collections.reverse(classList);
    return classList;
  }

  public static Optional<Method> getSupplierMethod(Object object)
  {
    List<Method> methods = Stream.of(object.getClass().getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(LoggableType.extractionMethod.class))
        .collect(toList());
    if (methods.size() > 1)
    {
      throw new IllegalStateException("There can't be more than one method annotated @LoggableType.extractionMethod");
    }


    return methods.stream().findFirst();
  }

  public static String getRenamedFieldNameOrDefault(Field field)
  {
    LoggableType.property annotation = field.getAnnotation(LoggableType.property.class);
    if (nonNull(annotation) && isNotEmpty(annotation.name()))
    {
      return annotation.name();
    }
    return field.getName();
  }

  public static boolean isRecursiveLoop(Map<Class, List<Class>> fieldsProcessedBefore, Field currentField)
  {
    if (isNull(currentField))
    {
      return false;
    }

    Optional<List<Class>> classes = Optional.ofNullable(fieldsProcessedBefore.get(currentField.getType()));
    if (classes.isPresent())
    {
      return classes.map(clazzes -> clazzes.contains(currentField.getDeclaringClass())).get();
    }

    return false;
  }
}
