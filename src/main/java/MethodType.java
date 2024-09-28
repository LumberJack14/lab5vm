public enum MethodType {//методы: многочлен Лагранжа, многочлен Ньютона с разделенными разностями, многочлен гаусса
    LAGRANGE("многочлен Лагранжа"),
    NEWTON("многочлен Ньютона с разделенными разностями"),
    GAUSS("многочлен Гаусса");

    private String name;

    MethodType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MethodType getMethodTypeByName(String name) {
        for (MethodType functionType : MethodType.values()) {
            if (functionType.getName().equalsIgnoreCase(name)) {
                return functionType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
