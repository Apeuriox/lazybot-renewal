package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageTimeDistribution;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UsageSVGMapper extends LazybotSVGMapper
{
    public static Document mapCommandUsageToPanel(CommandUsage commandUsage)
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("CommandUsage");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM. dd", Locale.ENGLISH);

        String formatted = commandUsage.getCreated_at().format(formatter);
        document.getElementById("timestamp").setTextContent(formatted);
        document.getElementById("totalinvokes").setTextContent(CommonTool.transformNumber(String.valueOf(commandUsage.getTotal())));

        if (commandUsage.getCommand() != null)
        {
            document.getElementById("maincommand").setTextContent(commandUsage.getCommand().getFirst().getCommand().toUpperCase());
            for (int i = 0; i < 6; i++)
            {
                if (i<commandUsage.getCommand().size())
                {
                    double percentage = (double) commandUsage.getCommand().get(i).getCount() /  (double) commandUsage.getTotal();
                    document.getElementById("commandName-" + i).setTextContent(commandUsage.getCommand().get(i).getCommand());
                    document.getElementById("count-" + i).setTextContent(CommonTool.formatNumber(commandUsage.getCommand().get(i).getCount()));
                    document.getElementById("count-per-" + i).setTextContent((commandUsage.getCommand().get(i).getCount() / commandUsage.getTotal() * 100) + "%");
                    document.getElementById("count-per-" + i).setTextContent(Math.round(percentage * 100) + "%");
                    int size = (int) Math.max(30,Math.round(percentage * 500));
                    document.getElementById("bar-" + i).setAttribute("width", String.valueOf(size));
                }
            }
        }

        if (commandUsage.getDistribution() != null) {
            int maxForDistribution = 0;
            for (LazybotUsageTimeDistribution distribution : commandUsage.getDistribution()) {
                if (distribution.getCount() > maxForDistribution)
                    maxForDistribution = distribution.getCount();
            }
            document.getElementById("label-mid").setTextContent(CommonTool.formatNumber(maxForDistribution/2));
            document.getElementById("label-full").setTextContent(CommonTool.formatNumber(maxForDistribution));



            for (int i = 0; i < 23; i++) {
                double percentage = (double) commandUsage.getDistribution().get(i).getCount() /  (double) maxForDistribution;
                document.getElementById("chart-" + i).setAttribute("height", String.valueOf(Math.round(percentage * 280)));
                document.getElementById("chart-" + i).setAttribute("y", String.valueOf(920 - Math.round(percentage * 280)));
            }
        }

        if (commandUsage.getSource() != null) {
            for (int i = 0; i < 6; i++) {
                if (i<commandUsage.getSource().size())
                {
                    int middleIndex = commandUsage.getSource().get(i).getName().length() / 2;
                    document.getElementById("circle-g-" + i).setAttribute("opacity", "1");
                    document.getElementById("label-full0-" + i).setTextContent(commandUsage.getSource().get(i).getName().substring(0, middleIndex));
                    document.getElementById("label-full1-" + i).setTextContent(commandUsage.getSource().get(i).getName().substring(middleIndex));
                }
            }
        }
        return document;
    }
}
