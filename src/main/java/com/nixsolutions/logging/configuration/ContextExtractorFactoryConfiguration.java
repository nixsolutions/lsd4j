package com.nixsolutions.logging.configuration;

import static java.util.Collections.EMPTY_MAP;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractorFactory;

@Configuration
public class ContextExtractorFactoryConfiguration
{
  private static final ContextParamExtractor<Object> DEFAULT_CONTEXT_PARAM_EXTRACTOR =
      new ContextParamExtractor<Object>()
      {
        @Override
        public Map<String, Object> extractParams(String name, Object parameter)
        {
          return EMPTY_MAP;
        }

        @Override
        public List<Class<?>> getExtractableClasses()
        {
          throw new UnsupportedOperationException();
        }
      };

  @Bean
  public ContextParamExtractorFactory contextParamExtractorFactory(
      @Autowired List<? extends ContextParamExtractor> contextParamExtractorsList,
      @Autowired(required = false) @Qualifier("defaultExtractor") ContextParamExtractor defaultExtractor)
  {
    ContextParamExtractorFactory contextParamExtractorFactory =
        new ContextParamExtractorFactory(contextParamExtractorsList);

    if (Objects.isNull(defaultExtractor))
    {
      contextParamExtractorFactory.setDefaultContextParamExtractor(DEFAULT_CONTEXT_PARAM_EXTRACTOR);
    }
    else
    {
      contextParamExtractorFactory.setDefaultContextParamExtractor(defaultExtractor);
    }

    return contextParamExtractorFactory;
  }
}
