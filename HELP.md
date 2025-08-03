# Lazybot 帮助页面

## 注意事项:

1.bot的命令前缀为`/`<br>
2.参数之间必须用`空格`间隔(除`/nameToId命令`)<br>
3.绝大部分命令需要绑定用户才能使用<br>
4.更改查询模式为 :后加数字，如:1为taiko模式，由于我（Aloic）不玩其他模式所以支持可能并不好<br>

> 参数中
`/`代表无参数传递
`{}`表示必须传递的参数
`[]`表示可选传递参数
`*num`表示阿拉伯数字
`&`表示使用旧设计面板

| 命令            | 别名     | 参数                            | 说明                                      | 举例                                    | 注意事项                                             |
| ------------- | ------ | ----------------------------- | --------------------------------------- | ------------------------------------- | ------------------------------------------------ |
| link          | /      | `{userName}`                  | 绑定用户名                                   | `/link Aloic`                         | 解绑请用/unlink                                      |
| unlink        | /      | `/`                           | 取消绑定                                    | `/unlink`                             | 多余参数会被忽略                                         |
| profile       | info   | `[userName]`                  | 查询个人资料                                  | `/profile Aloic`                      | /                                                |
| card          | /      | `[userName]`                  | 查询个人资料, 生成小型卡片样式                        | `/card Aloic`                         | /                                                |
| setmode       | /      | `{*num}`                      | 设置osu默认查询模式                             | `/setmode 1`                          | 0=std, 1=taiko, 2=fruits, 3=mania                |
| score         | /      | `[userName] {bid}+[mods] [&]` | 按照指定用户查询指定地图下的指定Mod组合的成绩                | `/score Aloic 3970329+HT`             | 若存在&则会使用旧设计面板                                    |
| recentPass    | rp, pr | `[userName] [#*num] [&]`      | 查询指定用户的最近Pass成绩中的第`*num`个               | `/pr Aloic #1 &`                      | 不填索引则默认为#1，若存在&则会使用旧设计面板                         |
| recent        | re     | `[userName] [#*num] [&]`      | 查询指定用户的最近游玩成绩中的第`*num`个                 | `/re Aloic`                           | 不填索引则默认为#1，若存在&则会使用旧设计面板                         |
| prs           | rps, ps| `[userName] [&]`               | 查询指定用户的最近Pass的21个成绩               | `/prs Aloic #1 &`                      | 输入`&`采用List形式返回                         |
| res        | rs     | `[userName] [&]`      | 查询指定用户的最近游玩的21个成绩                 | `/res Aloic`                           | 输入`&`采用List形式返回                           |
| todaybp       | tbp    | `[userName] [#*num]`          | 查询指定用户的`*num`天内的新增Bp                    | `/todaybp Aloic #10`                  | 不填索引则默认为#1                                       |
| bp            | /      | `[playerName] [[#]num] [&]`   | 查询指定用户的最佳成绩中的第`*num`个                   | `/bp Aloic #10`                       | 不填索引则默认为#1，若存在&则会使用旧设计面板                         |
| bps            | bs      | `[playerName] [&]`   | 查询指定用户的第1到第21的最佳成绩                   | `/bps Aloic`                       | 若存在&则会使用旧设计面板                         |
| bplist        | /      | `{*num-*num}`                 | 查询用户最佳成绩中的第`*num`到`*num`个               | `/bplist 1-100`                       | 暂不支持查询他人                                         |
| bpcard        | /      | `{*num-*num}`                 | 查询用户最佳成绩中的第`*num`到`*num`个，但是以Card列表形式返回 | `/bpcard 1-100`                       | 暂不支持查询他人                                         |
| noChoke       | nc     | `[userName]`                  | 将指定用户的BP 100按照FC重新计算，俗称幻想时刻             | `/noChoke Aloic`                      | 渲染出的图形暂时只有Fix后的BP                                |
| no1Miss       | /      | `[userName]`                  | 将指定用户的BP中<=1miss的成绩按照FC重新计算             | `/no1MIss Aloic`                      | 此为/noChoke的限制版                                   |
| bpvs          | /      | `{userName}`                  | 与指定用户的BP进行对比                            | `/bpvs Aloic`                         | 生成的图形为旧设计                                        |
| recommendDiff | rd     | `[userName]`                  | 查询指定用户的推荐星级                             | `/rd Aloic`                           | /                                                |
| ppmap         | /      | `[userName]`                  | 绘制指定用户的历史BP                             | `/ppmap Aloic`                        | 数据来源为OsuTrack，此功能并不是绘制你BP 100                    |
| update        | /      | `{Avatar或Track} [userName]`   | 更新指定用户的头像或ppmap数据                       | `/update avatar Aloic`                | /                                                |
| nameToId      | n2d    | `{userNameArray}`             | 将指定的用户名序列转化为UID                         | `/nameToId Aloic,UselessPlayer,Zh_Jk` | / 间隔符为`,`，与其他指令不同                                |
| bpif          | /      | `[userName] {operator}{mods}` | 按照指定的规则和mod重算用户的全部成绩，使用详情请看渲染结果提示       | `/bpif Aloic +HDHR`                   | 只渲染重算完成后的前30个成绩                                  |
| tips          | /      | `[id]`                        | 返回一个随机的Aloic小提示，输入ID可明确指定               | `/tips 38`                            | /                                                |
| topscores     | ts     | `[max]`                       | 查询指定模式的最高Pp成绩列表                         | `/ts 20 :1`                           | bancho查询不到的成绩会被跳过，请注意数据来源为osu track，除了std模式，均已过时 |
| whatif        | /      | `{pp}[*][count]`              | 假设你多刷了这么多pp后的总pp变化                      | `/whatif 300*10 400*5`                | 支持多项添加，但是数值不pp数值允许大于3000，同时会计算bp 200，和ATRI不同     |
| modInfo       | mod    | `{modName}`                   | 查询对应Mod的介绍                              | `/mod HR`                             | 还没做完                                             |
| allScores     | as     | `[userName] {bid}`            | 查询对应玩家在对应地图下的全部成绩                       | `/AllScores Aloic 668662`             | 成绩过多可能会被屏蔽                                       |
| plus          | ppp    | `[userName] [&]`              | 查询对应玩家的重算版pp+，输入&以Corsace样式输出结果         | `/plus Aloic &`                       | 颜色会跟随玩家主页颜色，默认为色域208的蓝色                          |
| addScore      | add    | `[username] {bid}`            | 以BID申请pp+重算，取最大结果                       | /addscore Aloic 668662                | 仅支持standard                                      |
| monitor      | /    | `/`            | 查询bot的指令使用情况                       | /monitor                | /                                      |


## 自定义命令:

| 父级命令      | 二级命令        | 参数                         | 说明                                                         | 举例                         | 注意事项                           |
| ----------- | ------------- | ------------- | ------------------------------------------------------------ | --------------------------------------- | --------------------------------- |
| customize   | profileBG     | `{URL}`        | 更改/profile的背景图片，URL为图片链接，提交后需要等待验证         | `/customize profilebg https://this.is.link`  |  图片需要为1900x1000，超出的部分会被裁剪  |
| customize   | profileTheme  | `{Light或Dark或Lighter}` | 更改/profile的颜色预设                                         | `/customize profileTheme Light`             | /                                  |

## 娱乐命令:

| 命令      | 别名        | 参数                         | 说明                                                         | 举例                         | 注意事项                           |
| ----------- | ------------- | ------------- | ------------------------------------------------------------ | --------------------------------------- | --------------------------------- |
| name   | /     | `{URL}`        | 猜猜别人的ID，数据来源为绑定次Bot的人，限时三分钟，输入/name &以提前结束        | `/name Aloic`  |  /  |                                 |
| song   | /     | `{URL}`        | 猜猜地图，数据来源为所有绑定此用户的玩家的随机一个的BP，限时5分钟，输入/song &以获取提示，输入/song &&以提前结束        | `/song`  |  一个群同时只能有一个 |                                 |




## 特别感谢
活跃开发者: Aloic
全部开发者（包括前身和现在）: LazyChildren, Aloic, Marisaya
技术支持: Zh_Jk, -Spring Night-, Atri1024
图形设计: Slimezzz, Aloic




