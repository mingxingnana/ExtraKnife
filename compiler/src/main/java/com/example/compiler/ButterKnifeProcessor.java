package com.example.compiler;

import com.example.annotations.Extra;
import com.example.compiler.util.InjectorInfo;
import com.example.compiler.util.InjectorInfoUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Administrator on 2018/4/10 0010.
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Extra.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    Map<String, List<VariableElement>> map = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Extra.class);

        for (Element element : elementSet) {

            VariableElement varElement = (VariableElement) element;
            String className = getParentClassName(varElement);
            List<VariableElement> cacheElements = map.get(className);
            if (cacheElements == null) {
                cacheElements = new LinkedList<>();
            }

            cacheElements.add(varElement);
            map.put(className, cacheElements);
        }

        generate();
        return false;
    }

    private void generate() {
        Iterator<Map.Entry<String, List<VariableElement>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<VariableElement>> entry = iterator.next();
            List<VariableElement> cacheElements = entry.getValue();
            if (cacheElements == null || cacheElements.size() == 0) {
                continue;
            }

            InjectorInfo info = InjectorInfoUtil.createInjectorInfo(processingEnv, cacheElements.get(0));

            final ClassName className = ClassName.get(info.packageName, info.classlName);
            final ClassName InterfaceName = ClassName.get("com.example.api", "InjectExtra");
            MethodSpec.Builder injectsBuilder = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(className, "target");
            for (VariableElement element : cacheElements) {
                Extra annotation = element.getAnnotation(Extra.class);
                String value = annotation.value();
                String fieldName = element.getSimpleName().toString();
                String type = element.asType().toString();


                addExtras(injectsBuilder, fieldName, type, value);

            }
            MethodSpec injects = injectsBuilder.build();

            TypeSpec typeSpec = TypeSpec.classBuilder(info.newClassName)
                    .addSuperinterface(ParameterizedTypeName.get(InterfaceName, className))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(injects)
                    .build();

            JavaFile javaFile = JavaFile.builder(info.packageName, typeSpec).build();


            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }

    private void addExtras(MethodSpec.Builder injectsBuilder, String fieldName, String type, String value) {
//        injectsBuilder.addStatement("target." + fieldName + " = (" + type + ")(target).getIntent().getStringExtra(\"" + value + "\")");

        if ("".equals(value) || null == value) {
            value = fieldName;
        }


        if (type.contains("List")) {
            injectsBuilder.addStatement("target." + fieldName + " = (" + type + ")(target).getIntent().getSerializableExtra(\"" + value + "\")");
        } else {
            String beginTarget = "target." + fieldName + " = target.getIntent().";
            if (type.contains("String"))
                injectsBuilder.addStatement(beginTarget + "getStringExtra(\"" + value + "\")");
            if (type.equals("int"))
                injectsBuilder.addStatement(beginTarget + "getIntExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("int[]"))
                injectsBuilder.addStatement(beginTarget + "getIntArrayExtra(\"" + value + "\")");
            if (type.equals("long"))
                injectsBuilder.addStatement(beginTarget + "getLongExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("long[]"))
                injectsBuilder.addStatement(beginTarget + "getLongArrayExtra(\"" + value + "\")");
            if (type.equals("double"))
                injectsBuilder.addStatement(beginTarget + "getDoubleExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("double[]"))
                injectsBuilder.addStatement(beginTarget + "getDoubleArrayExtra(\"" + value + "\")");
            if (type.equals("float"))
                injectsBuilder.addStatement(beginTarget + "getFloatExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("float[]"))
                injectsBuilder.addStatement(beginTarget + "getFloatArrayExtra(\"" + value + "\")");
            if (type.equals("byte"))
                injectsBuilder.addStatement(beginTarget + "getByteExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("byte[]"))
                injectsBuilder.addStatement(beginTarget + "getByteArrayExtra(\"" + value + "\")");
            if (type.equals("boolean"))
                injectsBuilder.addStatement(beginTarget + "getBooleanExtra(\"" + value + "\",target." + fieldName + ")");
            if (type.equals("boolean[]"))
                injectsBuilder.addStatement(beginTarget + "getBooleanArrayExtra(\"" + value + "\" )");
            if (type.contains("Bundle"))
                injectsBuilder.addStatement(beginTarget + "getBundleExtra(\"" + value + "\" )");
        }
    }


    private String getPackageName(ProcessingEnvironment processingEnv, TypeElement varElement) {
        return processingEnv.getElementUtils().getPackageOf(varElement).getQualifiedName().toString();

    }

    private String getParentClassName(VariableElement varElement) {

        TypeElement typeElement = (TypeElement) varElement.getEnclosingElement();

        String packageName = getPackageName(processingEnv, typeElement);
        return packageName + "." + typeElement.getSimpleName().toString();
    }
}
