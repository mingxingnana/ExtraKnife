package com.example.compiler.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;


public class InjectorInfoUtil {

    public static InjectorInfo createInjectorInfo(ProcessingEnvironment mProcessingEnv, VariableElement element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String packageName = AnnotationUtil.getPackageName(mProcessingEnv, typeElement);
        String className = typeElement.getSimpleName().toString();
        return new InjectorInfo(packageName, className);
    }
}
