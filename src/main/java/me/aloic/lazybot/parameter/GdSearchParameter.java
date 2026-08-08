//package me.aloic.lazybot.parameter;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import me.aloic.lazybot.exception.LazybotRuntimeException;
//
//import java.util.List;
//
///**
// * GD关卡搜索参数
// * 对应 gd查询 命令的选项
// */
//@EqualsAndHashCode(callSuper = true)
//@Data
//@NoArgsConstructor
//public class GdSearchParameter extends LazybotCommandParameter
//{
//    /** 搜索文本 (关卡名) */
//    private String searchString;
//
//    /** 搜索类型, 默认0 */
//    private Integer type;
//
//    /** 非demon难度过滤: -2=禁用, 1=Easy ~ 5=Insane */
//    private Integer diff;
//
//    /** Demon难度过滤: null=禁用, 1=EzD ~ 5=ExD, 0=HardDemon (GD原始值) */
//    private Integer demonFilter;
//
//    /** false=仅rated关卡, true=所有关卡 */
//    private Boolean all;
//
//    /** 长度过滤: null=不限, 0=Tiny ~ 4=XL, 5=Plat */
//    private Integer length;
//
//    /** 当前页码, 默认0 */
//    private Integer page;
//
//    public GdSearchParameter(String searchString)
//    {
//        this.searchString = searchString;
//        this.type = 0;
//        this.all = false;
//        this.page = 0;
//    }
//
//    @Override
//    public void validateParams()
//    {
//        if (searchString == null || searchString.isBlank()) {
//            throw new LazybotRuntimeException("搜索参数为空，请重新尝试哦~");
//        }
//        if (!searchString.matches("^[A-Za-z0-9\\s]+$")) {
//            throw new LazybotRuntimeException("搜索参数只能为英文或数字哦~");
//        }
//        if (type == null) type = 0;
//        if (all == null) all = false;
//        if (page == null) page = 0;
//    }
//
//    /**
//     * 从命令参数列表解析 GdSearchParameter
//     * 格式: [选项] <搜索文本>
//     * 选项: -d <demonFilter>  -u <diff>  -a  -l <length>  -t <type>
//     */
//    public static GdSearchParameter analyzeParameter(List<String> params)
//    {
//        GdSearchParameter p = new GdSearchParameter();
//        if (params == null || params.isEmpty()) {
//            return p; // validateParams will catch empty searchString
//        }
//
//        StringBuilder searchBuilder = new StringBuilder();
//        for (int i = 0; i < params.size(); i++) {
//            String token = params.get(i);
//            switch (token) {
//                case "-d":
//                    if (i + 1 < params.size()) {
//                        try { p.demonFilter = Integer.parseInt(params.get(++i)); }
//                        catch (NumberFormatException e) { searchBuilder.append(token).append(" "); }
//                    }
//                    break;
//                case "-u":
//                    if (i + 1 < params.size()) {
//                        try { p.diff = Integer.parseInt(params.get(++i)); }
//                        catch (NumberFormatException e) { searchBuilder.append(token).append(" "); }
//                    }
//                    break;
//                case "-a":
//                    p.all = true;
//                    break;
//                case "-l":
//                    if (i + 1 < params.size()) {
//                        try { p.length = Integer.parseInt(params.get(++i)); }
//                        catch (NumberFormatException e) { searchBuilder.append(token).append(" "); }
//                    }
//                    break;
//                case "-t":
//                    if (i + 1 < params.size()) {
//                        try { p.type = Integer.parseInt(params.get(++i)); }
//                        catch (NumberFormatException e) { searchBuilder.append(token).append(" "); }
//                    }
//                    break;
//                default:
//                    if (!searchBuilder.isEmpty()) searchBuilder.append(" ");
//                    searchBuilder.append(token);
//                    break;
//            }
//        }
//        p.searchString = searchBuilder.toString().trim();
//        return p;
//    }
//
//    /**
//     * 构建GD API请求参数 (URL form encoded body)
//     */
//    public String buildPostBody()
//    {
//        int star = all ? 0 : 1;
//        StringBuilder body = new StringBuilder();
//        body.append("str=").append(searchString);
//        body.append("&type=").append(type != null ? type : 0);
//        body.append("&secret=Wmfd2893gb7");
//        body.append("&page=").append(page != null ? page : 0);
//        if (demonFilter != null) {
//            body.append("&diff=-2");
//            body.append("&demonFilter=").append(demonFilter);
//        } else if (diff != null) {
//            body.append("&diff=").append(diff);
//        }
//        body.append("&star=").append(star);
//        if (length != null) {
//            body.append("&len=").append(length);
//        }
//        return body.toString();
//    }
//}
