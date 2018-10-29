package com.nixsolutions.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoggableType {
  boolean ignoreParents() default true;

  ExtractionResolutionStrategy resolutionStrategy() default ExtractionResolutionStrategy.COLLECTOR_FIRST;

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.METHOD})
  @interface property {
    String name() default "";
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD})
  @interface extractionMethod {

  }
}
