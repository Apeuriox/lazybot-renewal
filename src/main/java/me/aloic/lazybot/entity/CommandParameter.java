package me.aloic.lazybot.entity;

public record CommandParameter(String name, String type, String description,
                               me.aloic.lazybot.entity.CommandParameter.ParameterType optional)
{
    public enum ParameterType
    {
        REQUIRED, OPTIONAL;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        if (optional == ParameterType.REQUIRED)
            sb.append("必选");
        else
            sb.append("可选");
        sb.append("): ").append(description);
        return sb.toString();
    }
}
