package org.example.parameter;

public enum EFieldOperator {

    greaterThan(">"),
    greaterEqualThan(">:"),
    lessThan("<"),
    lessEqualThan("<:"),
    equal(":"),
    notEqual("!:"),
    in("in"),
    notIn("!in"),
    like("like"),
    unlike("unlike"),
    isnull("isnull"),
    notnull("notnull"),
    jspe("jspe"), //jsonSinglePathEqual
    jos("jos"), //jsonOneSearch
    jas("jas"); //jsonAllSearch

    ;

    public String operator;

    EFieldOperator(String operator) {
        this.operator = operator;
    }

    public static EFieldOperator getByName(String name) {
        for (EFieldOperator operator : EFieldOperator.values()) {
            if (operator.operator.equals(name)) {
                return operator;
            }
        }
        return null;
    }
}
