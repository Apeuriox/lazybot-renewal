//package me.aloic.lazybot;
//
//import me.aloic.lazybot.shiro.utils.MessageEventFactory;
//import me.aloic.lazybot.util.CommonTool;
//import org.junit.jupiter.api.Test;
//
//public class StaticFunctionTest
//{
//    @Test
//    public void testOc()
//    {
//        String input = "unlink ";
//        System.out.println(formatCommand(input));
//    }
//    private static String formatCommand(String s)
//    {
//        s = s.replace("！","!").trim();
//        s = s.replace("：",":");
//        StringBuffer sb = new StringBuffer(s);
//        for (int i = 0; i < sb.length(); i++) {
//            if((sb.charAt(i) == ':' && sb.charAt(i - 1) != ' ')||(sb.charAt(i) == '&' && sb.charAt(i - 1) != ' ')){
//                sb.insert(i, ' ');
//                i++;
//            }
//            else if(sb.charAt(i) == ' ' && sb.charAt(i + 1) ==' '){
//                sb.deleteCharAt(i);
//                i--;
//            }
//        }
//        return sb.toString().trim().toLowerCase();
//    }
//}
