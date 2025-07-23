# 更新日志

## Lazybot V1.1.35/20250722

- 新增`/AddScore`功能，用于以Bid申请添加计算pp+数据。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/plus`功能新增了一套新的面板设计，原型来自Corsace Open 2024，输入`/plus &`即可使用。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/plus`添加了模式限制。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/plus`新增了一套标签以用于描述顶尖玩家。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了在`/ppmap`中直接指定用户名的输入被错误忽略的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了`/plus`中错误捕获异常导致的输出结果错误。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了`/info`命令中不活跃玩家的Rank被错误的显示为null的问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)

## Lazybot V1.1.28/20250613
- 新增`/plus`功能，用于查询用户的pp+数据。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构了**ApiRequestStarter**和**DataObjectExtractor**，现在使用依赖注入归为Spring管理，以添加token失效重试机制。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/AllScores`添加了四模式支持，同时将Std中的SpeedNote改为LengthBonus。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了成绩面板中意外出现CL mod的问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.1.21/20250605
- 新增`/AllScores`功能，用于查询用户在指定地图下的全部成绩，采用全新的面板设计。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构所有Mod以及Rank的颜色到新的枚举中。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了使用`/profile`命令时，Bp中过长的标题溢出指定元素的问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了ResourceMonitor与Jar包中提取静态文件时跳过执行的问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了玩家在回退用户名后无法使用部分指令的问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.1.8/20250511
- 新增`/mod`功能，用于查询osu游戏mod的详细解释。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将所有Lazer Mod添加到OsuMod.java，并为其添加别名属性。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.1.6/20250502
- 现已将`/bplist`和`/bpcard`的索引上限增加至200，同时添加了同时最大渲染数量为100。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将`/bpvs`指令中**CompletableFuture**执行中抛出的**ExecutionException**异常处理移动到**SlashCommandProcessor**中，并添加包名分割。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了当玩家使用Lazer中的Difficulty Adjust时，没有修改全部四维导致原始值被Null覆盖，进而使得计算DT/HT后的AR、OD触发空指针的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了在`/bpvs`中能够对比同一个用户不同用户名的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.1.5/20250425
- 将`/noChoke` 和 `/no1Miss` 重构为一个类。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 更新了pp算法以匹配Bancho现在部署的版本。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 现已将`/bp`的索引上限提升到200。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构了`/bpif`、`/bpvs`、`/bplist`、`/bpcard`、`/noChoke`内部为异步多线程处理图片缓存以及pp重算。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为所有指令添加了测试接口。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 删除了HTTP请求拦截器。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将图片渲染的默认格式从PNG换到JPG。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了**VirtualThreadExecutor**线程池未正常复用的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.0.8/20250408
- 为`/tips`的输出添加了索引文本。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了`/ts`指令中有时会错误跳过元素导致索引混乱。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加了HTTP请求拦截器。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.0.7/20250303
- 重构线程池配置为虚拟线程池。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>

## Lazybot V1.0.6/20250228
- 修复了接受到的指令文本结尾额外的空格导致下标越界问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 更新了`/help`页面。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 删除了未使用的ORM Mapper层配置。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了ppy把玩家最近游玩的请求返回结果的默认索引最大值从50降低为5导致的`/pr`、`/re`指令找不到索引为5之外成绩的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 现在图片以base64发送。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了与Spring交互中的Async问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 完善了异常处理机制。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>










