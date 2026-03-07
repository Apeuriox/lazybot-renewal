package me.aloic.lazybot.parameter;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviationFittingParameter extends LazybotCommandParameter
{
    private Double overallDifficulty =10D;
    private Double targetUnstableRate;


    @Override
    public void validateParams()
    {
        if (overallDifficulty>13.33)
        {
            throw new IllegalArgumentException("OD值超过13.33时实际判定区间为负数");
        }
    }
    public static DeviationFittingParameter analyzeParameter(List<String> params)
    {
        DeviationFittingParameter parameter=new DeviationFittingParameter();
        if (params == null || params.isEmpty())
            throw new IllegalArgumentException("输入参数为空，请至少输入ur值");

        String text = String.join(" ", params).trim();
        Matcher odMatcher = Pattern.compile("od\\s*(\\d+(?:\\.\\d+)?)").matcher(text);
        if (odMatcher.find()) {
            parameter.overallDifficulty = Double.parseDouble(odMatcher.group(1));
        }

        Matcher urMatcher = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*ur").matcher(text);
        if (urMatcher.find()) {
            parameter.targetUnstableRate = Double.parseDouble(urMatcher.group(1));
        }

        Matcher numMatcher = Pattern.compile("\\d+(?:\\.\\d+)?").matcher(text);
        List<Double> numbers = new ArrayList<>();

        while (numMatcher.find()) {
            numbers.add(Double.parseDouble(numMatcher.group()));
        }

        if (parameter.targetUnstableRate != null) {
            numbers.remove(parameter.targetUnstableRate);
        }
        if (odMatcher.find(0)) {
            numbers.remove(parameter.overallDifficulty);
        }

        if (parameter.targetUnstableRate == null) {
            if (numbers.size() == 1) {
                parameter.targetUnstableRate = numbers.getFirst();
            } else if (numbers.size() >= 2) {
                parameter.targetUnstableRate = numbers.get(0);
                parameter.overallDifficulty = numbers.get(1);
            }
        }

        if (parameter.targetUnstableRate == null) {
            throw new IllegalArgumentException("UR值为必要值，当前未指定");
        }
        return parameter;
    }
    public static void setupDefaultValue(DeviationFittingParameter parameter, @NonNull String mode)
    {
        if (parameter.getMode() == null)
            parameter.setMode(mode);
        if (parameter.getTargetUnstableRate() == null)
            throw new IllegalArgumentException("UR值为必要值，当前未指定");

    }
}
