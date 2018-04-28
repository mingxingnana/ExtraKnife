package com.example.compiler.util;

import java.io.File;


public class InjectorInfo {
    public static final String SUFFIX = "InjectExtra";
    public String packageName;

    public String classlName;

    public String newClassName;

    public InjectorInfo(String packageName, String classlName) {
        this.packageName = packageName;
        newClassName = classlName + "_" + SUFFIX;
        this.classlName = classlName;
    }

    public String getClassFullPath() {
        return packageName + File.separator + newClassName;
    }
}
