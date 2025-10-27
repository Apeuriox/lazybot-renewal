package me.aloic.lazybot.entity.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LazybotMessageWithImage
{
    private byte[] image;
    private String message;
    public LazybotMessageWithImage(String message)
    {
        this.message = message;
    }
}
