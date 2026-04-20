# 更新日志

## Lazybot V1.1.81/20260420

- 新增`/年度总结`功能。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/ur`功能，输入指定OD和UR，以正态分布估计Acc值。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/Pb`功能，用户可通过他查询自己的 **PP+** 最好成绩列表。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- - 新增`/SetPanel`功能，用以设定默认成绩面板。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了`/p`被错误传递为`/r`的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为星数大于9的难度添加了新的颜色。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加了愚人节版的 **PP+** 卡片以及成绩面板。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- **Trimmed Card**已对所有人开放，同时添加了部分设置参数。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/Link`添加了限制，现在用户不能随意绑定知名人物，同时绑定已绑定用户的账户不会再返回该用户的绑定数据。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 更新了**Rosu-pp**本地计算器。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 更新了**Springboot**及其相关依赖。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>


## Lazybot V1.1.74/20251201

- 新增对**Star Moon**私服的支持。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增**Badge Challenge**系统，功能类似于Map Pack，用户完成一系列挑战后可获得Badge。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了使用`/update track`时，远程请求返回错误导致反序列化出错无法正常返回结果的问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将地图背景的默认获取改为Sayobot，将ppy官方的获取作为fallback方法，此改动是因为ppy偷偷摸摸改了图片获取方法才不得不改的。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构了**PlayerService**的所有方法，将大部分方法的返回类型由二进制图片数据转为渲染所需的数据结构，同时将图片渲染操作移动至**Command**层，调用**RendererDistributor**类的静态方法渲染以增加灵活性。作者: [Aloic](https://osu.ppy.sh/users/11232623)


## Lazybot V1.1.65/20251027

- 新增Badge系统，和Redeem系统。 作者: [Aloic](https://osu.ppy.sh/users/11232623)
- 新增`/ScoreRank`功能，用以查询指定图下指定QQ群聊内所有绑定Lazybot用户的成绩。 作者: [LazyChildren](https://osu.ppy.sh/users/14697856)<br>
-  为Quadra Grid面板添加不兼容模组提示。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加了头像自动更新逻辑。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/name`指令添加一个只显示当前群聊玩家的变种。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 移除了ScoreVO对象中的字符串列表兼容层，现已采用更现代的方式。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 移除了所有额外的Apache Batik Node创建。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了DarkScore中不正确的星级文本颜色问题。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了DarkScore中超过10星的星级的错误对齐问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)

## Lazybot V1.1.60/20251016

- 新增Quadra Grid成绩面板样式，用以查询成绩的PP+详情以及PP详情。 作者: [Aloic](https://osu.ppy.sh/users/11232623)

- 新增`/Oa`功能，用以查询用户的Osu头像。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
-  添加了`/Thumbnail`功能，用于生成Alivemaster样式的视频封面。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为Moelleux样式卡片添加低饱和度模式。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加了头像自动更新逻辑。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为Moelleux添加了色相覆盖功能。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加了缩放版Moelleux卡片，仅支持HTTP获取。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 移除了所有额外的\[Lazybot\]表示。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了将`/card`中纯数字名称过滤后文本为空导致的数组越界问题。作者: [Aloic](https://osu.ppy.sh/users/11232623)

## Lazybot V1.1.52/20250903

- 新增`/Filter`功能，以给定的条件过滤用户的BP 200数据。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/CheckIn`功能，用户签到功能，使用新日历面板。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/Compare`功能，在其他用户查询单个成绩时快速返回自己的结果。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重写了图形滤镜以满足**Gameboy, GameGadget**面板的功能。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 为`/Card`添加了**Moelleux**样式卡片，限制暂时不可公开调用。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重写调用命令逻辑，现在以责任链模式调用。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构所有帮助页面到责任链中。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 添加权限控制，使其可单独禁用命令，作用域，调用人。 作者: [Aloic](https://osu.ppy.sh/users/11232623)


## Lazybot V1.1.43/20250803

- 新增`/Song`娱乐功能，随机挑选玩家BP进行裁剪以让玩家识别，即猜歌功能。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/Name`娱乐功能，随机挑选玩家的缓存名混淆后供玩家猜测，即猜名字功能。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/bs`、`/ps`、`/rs`功能，即以列表形式默认返回索引1到21的数据。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 新增`/monitor`功能，用于监控bot的指令使用情况，采用全新设计。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 重构了`/bp`/`/score`、和`/todaybp`的参数解析逻辑，以支持带空格的用户名。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将绘图模块拆解到对应的**SVGMappe**类中。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 将`/nochoke`以及`/no1miss`的计算上限提升到bp 200。 作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 修复了将`/nochoke`和`/no1miss`中错误的pp变化值。作者: [Aloic](https://osu.ppy.sh/users/11232623)<br>
- 细分了所有命令至不同的包中。作者: [Aloic](https://osu.ppy.sh/users/11232623)

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










