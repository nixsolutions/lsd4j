package com.nixsolutions.logging.parameters.loggabletype;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

public class AnnotatedObject<A extends Annotation>
{

  private Object object;
  private A annotation;

  private AnnotatedObject(Object object, A annotation)
  {
    this.object = object;
    this.annotation = annotation;
  }

  public static <A extends Annotation> AnnotatedObject<A> createWithAnnotation(Object object, Class<A> annotationClass)
  {
    return new AnnotatedObject<>(object, object.getClass().getAnnotation(annotationClass));
  }

  public static <A extends Annotation> AnnotatedObject<A> createWithAnnotationMethod(Object object, Supplier<A>
      getAnnotationMethod)
  {
    return new AnnotatedObject<>(object, getAnnotationMethod.get());
  }

  public boolean isAnnotated()
  {
    return annotation != null;
  }

  public Object getObject()
  {
    return object;
  }

  public A getAnnotation()
  {
    return annotation;
  }

  public Class getObjectClass()
  {
    return object.getClass();
  }
}
