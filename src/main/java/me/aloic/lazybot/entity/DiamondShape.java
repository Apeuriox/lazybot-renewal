package me.aloic.lazybot.entity;

import java.awt.geom.Path2D;

public class DiamondShape extends Path2D.Double
{

    public DiamondShape(double width, double height)
    {
        moveTo(0, height / 2);
        lineTo(width / 2, 0);
        lineTo(width, height / 2);
        lineTo(width / 2, height);
        closePath();
    }
}
