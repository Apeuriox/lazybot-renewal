package me.aloic.lazybot.entity;

public record CommandParameter(String name, String description,
                               me.aloic.lazybot.entity.CommandParameter.ParameterType type)
{
    public enum ParameterType
    {
        MUST, OPTIONAL;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        if (type == ParameterType.MUST)
            sb.append("必选");
        else
            sb.append("可选");
        sb.append("): ").append(description);
        return sb.toString();
    }
}
