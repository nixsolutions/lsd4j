package com.nixsolutions.logging.parameters.extractor;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ContextParamExtractorFactory
{
  private ContextParamExtractor<Object> defaultContextParamExtractor;

  private Map<Class<?>, ContextParamExtractor<?>> contextParamExtractors;

  public ContextParamExtractorFactory(List<? extends ContextParamExtractor> contextParamExtractorsList)
  {
    contextParamExtractors = createParameterExtractorMap(contextParamExtractorsList);
  }

  private Map<Class<?>, ContextParamExtractor<?>> createParameterExtractorMap(
      List<? extends ContextParamExtractor> contextParamExtractors)
  {
    return contextParamExtractors.stream()
        .flatMap(this::getParamExtractorsEntries)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private Stream<Entry<Class<?>, ? extends ContextParamExtractor<?>>> getParamExtractorsEntries(
      ContextParamExtractor<?> contextParamExtractor)
  {
    return contextParamExtractor.getExtractableClasses().stream()
        .map(clazz ->
            new SimpleEntry<Class<?>, ContextParamExtractor<?>>(clazz, contextParamExtractor));
  }

  public <E> ContextParamExtractor<E> getExtractorByClassSafe(Class<E> clazz)
  {
    return Optional.ofNullable(contextParamExtractors.get(clazz))
        .orElse((ContextParamExtractor) defaultContextParamExtractor);
  }

  public <E> ContextParamExtractor<E> getExtractorByClass(Class<E> clazz)
  {
    return (ContextParamExtractor<E>) contextParamExtractors.get(clazz);
  }

  public void setDefaultContextParamExtractor(ContextParamExtractor<Object> defaultContextParamExtractor)
  {
    this.defaultContextParamExtractor = defaultContextParamExtractor;
  }
}
