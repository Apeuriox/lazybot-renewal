@import me.aloic.lazybot.osu.dao.entity.view.ModWikiPoster
@param ModWikiPoster poster
@param String modes
<?xml version="1.0" encoding="UTF-8"?>
<svg width="1000" height="${poster.height()}" viewBox="0 0 1000 ${poster.height()}" fill="none" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
<defs>
$unsafe{modes}
</defs>
<rect width="1000" height="100" fill="#35333E"/>
<rect y="100" width="1000" height="${poster.height() - 100}" fill="#292730"/>
<text fill="#E7E7E8" style="white-space: pre" xml:space="preserve" font-family="${poster.titleFont()}" font-size="${poster.titleSize()}" font-weight="700" letter-spacing="0em"><tspan x="24" y="64.385">${poster.title()}</tspan></text>
@for(var mode : poster.headerModes())
<use xlink:href="#${mode.id()}" transform="${mode.transform()}" fill="#E7E7E7"/>
@endfor
<g id="ranked" opacity="${poster.badge().ranked()}" transform="${poster.badge().transform()}">
<rect x="230" y="36" width="101" height="32" rx="16" fill="#A6EA64"/>
<text fill="#424139" style="white-space: pre" xml:space="preserve" font-family="Rubik" font-size="20" font-weight="600" letter-spacing="0em"><tspan x="241" y="58.85">RANKED</tspan></text>
</g>
<g id="partial-ranked" opacity="${poster.badge().partial()}" transform="${poster.badge().transform()}">
<rect x="230" y="36" width="191" height="32" rx="16" fill="#DBFF57"/>
<text fill="#424139" style="white-space: pre" xml:space="preserve" font-family="Rubik" font-size="20" font-weight="600" letter-spacing="0em"><tspan x="241" y="58.85">PARTIAL RANKED</tspan></text>
</g>
<g id="unranked" opacity="${poster.badge().unranked()}" transform="${poster.badge().transform()}">
<rect x="230" y="36" width="128" height="32" rx="16" fill="#FFCC24"/>
<text fill="#424139" style="white-space: pre" xml:space="preserve" font-family="Rubik" font-size="20" font-weight="600" letter-spacing="0em"><tspan x="239" y="59.85">UNRANKED</tspan></text>
</g>
@if(poster.unranked().present())
<text fill="#E6B3E2" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="22" font-weight="500" letter-spacing="0em"><tspan x="${poster.unranked().openX()}" y="58.85">(非</tspan></text>
@for(var icon : poster.unranked().icons())
<use xlink:href="#${icon.id()}" transform="${icon.transform()}" fill="#E7E7E7"/>
@endfor
<text fill="#E6B3E2" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="22" font-weight="500" letter-spacing="0em"><tspan x="${poster.unranked().closeX()}" y="58.85">)</tspan></text>
@endif
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="26" font-weight="500" letter-spacing="0em"><tspan x="27" y="154.405">图标: </tspan></text>
<path d="M113.124 125.29C114.175 123.362 116.195 122.163 118.391 122.163H147.922C150.118 122.163 152.138 123.362 153.189 125.29L160.913 139.45C161.89 141.241 161.89 143.405 160.913 145.196L153.189 159.356C152.138 161.284 150.118 162.483 147.922 162.483H118.391C116.195 162.483 114.175 161.284 113.124 159.356L105.4 145.196C104.423 143.405 104.423 141.241 105.4 139.45L113.124 125.29Z" fill="${poster.iconColor()}"/>
@if(poster.iconPresent())
<g transform="${poster.iconTransform()}" opacity="0.74" fill="black">
<g transform="${poster.iconAdjust()}">
$unsafe{poster.iconMarkup()}
</g>
</g>
@endif
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.abbrevY()}">${poster.abbrevLabel()}</tspan><tspan font-family="Rubik">${poster.acronym()}</tspan></text>
<text style="white-space: pre" xml:space="preserve" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.typeY()}" fill="#DAD4ED" font-family="Noto Sans SC">${poster.typeLabel()}</tspan><tspan fill="${poster.typeColor()}" font-family="${poster.typeFont()}">${poster.typeValue()}</tspan></text>
@if(poster.multipliersPresent())
@for(var line : poster.multiplierLines())
@if(!line.textX().isBlank())
@if(line.lead())
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${line.y()}">${poster.multiplierLabel()}</tspan></text>
@endif
@for(var icon : line.icons())
<use xlink:href="#${icon.id()}" transform="${icon.transform()}" fill="#E7E7E7"/>
@endfor
<text style="white-space: pre" xml:space="preserve" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${line.textX()}" y="${line.y()}" fill="#DAD4ED" font-family="Rubik">: </tspan>@for(var span : line.spans())<tspan fill="${span.fill()}" font-family="${span.font()}">${span.text()}</tspan>@endfor</text>
@else
<text style="white-space: pre" xml:space="preserve" font-size="20" font-weight="500" letter-spacing="0em">@if(line.lead())<tspan x="${poster.labelX()}" y="${line.y()}" fill="#DAD4ED" font-family="Noto Sans SC">${poster.multiplierLabel()}</tspan>@endif@for(var span : line.spans())<tspan fill="${span.fill()}" font-family="${span.font()}">${span.text()}</tspan>@endfor</text>
@endif
@endfor
@endif
<rect x="${poster.hotkeyChip().x()}" y="${poster.hotkeyChip().y()}" width="${poster.hotkeyChip().width()}" height="${poster.hotkeyChip().height()}" rx="4" fill="#1C1B17"/>
<text style="white-space: pre" xml:space="preserve" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.hotkeyY()}" fill="#DAD4ED" font-family="Noto Sans SC">${poster.hotkeyLabel()}</tspan><tspan x="${poster.hotkeyX()}" fill="#F6F1E8" font-family="${poster.hotkeyFont()}">${poster.hotkey()}</tspan></text>
@if(poster.effectsPresent())
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.effectsY()}">${poster.effectsLabel()}</tspan></text>
@for(var effect : poster.effects())
@for(var icon : effect.icons())
<use xlink:href="#${icon.id()}" transform="${icon.transform()}" fill="#E7E7E7"/>
@endfor
@for(var line : effect.lines())
<text fill="white" style="white-space: pre" xml:space="preserve" font-size="18" letter-spacing="0em">@if(line.lead())<tspan x="${effect.textX()}" y="${line.y()}" font-family="Rubik">: </tspan><tspan font-family="${line.font()}">${line.text()}</tspan>@else<tspan x="${line.x()}" y="${line.y()}" font-family="${line.font()}">${line.text()}</tspan>@endif</text>
@endfor
@endfor
@endif
@if(poster.shotsPresent())
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.shotsY()}">${poster.shotsLabel()}</tspan></text>
@for(var shot : poster.shots())
<image x="${shot.x()}" y="${shot.y()}" width="${shot.width()}" height="${shot.height()}" preserveAspectRatio="xMidYMid meet" xlink:href="${shot.href()}"/>
@if(shot.captionPresent())
<text fill="white" text-anchor="middle" style="white-space: pre" xml:space="preserve" font-family="${shot.captionFont()}" font-size="18" letter-spacing="0em"><tspan x="${shot.captionX()}" y="${shot.captionY()}">${shot.caption()}</tspan></text>
@endif
@endfor
@endif
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.incompatibleY()}">${poster.incompatibleLabel()}</tspan></text>
@if(poster.incompatiblePresent())
@for(var pill : poster.pills())
<rect x="${pill.x()}" y="${pill.y()}" width="${pill.width()}" height="26" rx="13" fill="${pill.fill()}"/>
<text fill="#394246" style="white-space: pre" xml:space="preserve" font-family="${pill.font()}" font-size="18" font-weight="700" letter-spacing="0em"><tspan x="${pill.textX()}" y="${pill.textY()}">${pill.text()}</tspan></text>
@endfor
@else
<text fill="#DAD4ED" style="white-space: pre" xml:space="preserve" font-family="Noto Sans SC" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.incompatibleNoneX()}" y="${poster.incompatibleY()}">无</tspan></text>
@endif
@if(poster.extraPresent())
<text style="white-space: pre" xml:space="preserve" font-size="20" font-weight="500" letter-spacing="0em"><tspan x="${poster.labelX()}" y="${poster.extraY()}" fill="#DAD4ED" font-family="Noto Sans SC">${poster.extraLabel()}</tspan>@for(var line : poster.extraLines())@if(line.lead())<tspan fill="white" font-family="${line.font()}">${line.text()}</tspan>@endif@endfor</text>
@for(var line : poster.extraLines())
@if(!line.lead())
<text fill="white" style="white-space: pre" xml:space="preserve" font-family="${line.font()}" font-size="20" letter-spacing="0em"><tspan x="${line.x()}" y="${line.y()}">${line.text()}</tspan></text>
@endif
@endfor
@endif
</svg>
