@import me.aloic.lazybot.graphics.help.CommandHelpPoster
@param CommandHelpPoster poster
<?xml version="1.0" encoding="UTF-8"?>
<svg width="1500" height="${poster.height()}" fill="none" xmlns="http://www.w3.org/2000/svg">
<g filter="url(#filter0_n_2594_2342)">
<rect id="totalBG" width="1500" height="${poster.height()}" fill="#E7E8E3"/>
</g>
    <g id="horizontal-line">
        @for(int y = 149; y < 2086 + poster.shift(); y += 150)
        <line y1="${y}" x2="1500" y2="${y}" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        @endfor
    </g>

    <g id="vertical-line">
        <line x1="149" y1="${poster.height()}" x2="149" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="299" y1="${poster.height()}" x2="299" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="449" y1="${poster.height()}" x2="449" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="599" y1="${poster.height()}" x2="599" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="749" y1="${poster.height()}" x2="749" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="899" y1="${poster.height()}" x2="899" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="1049" y1="${poster.height()}" x2="1049" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="1199" y1="${poster.height()}" x2="1199" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
        <line x1="1349" y1="${poster.height()}" x2="1349" stroke="black" stroke-opacity="0.06" stroke-width="2"/>
    </g>

@for(var line : poster.underHalftone())
<text fill="${line.fill()}" text-anchor="${line.anchor()}" style="white-space: pre" xml:space="preserve" font-family="${line.fontFamily()}" font-size="${line.fontSize()}" letter-spacing="${line.letterSpacing()}"><tspan x="${line.x()}" y="${line.y()}">${line.text()}</tspan></text>
@endfor

<text fill="#3A3C2E" text-anchor="end" style="white-space: pre" xml:space="preserve" font-family="Rubik" font-size="22" letter-spacing="0em"><tspan x="1436" y="95.535">@@LAZYBOT</tspan></text>
<mask id="mask0_2594_2342" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="1500" height="${poster.height()}">
<rect width="1500" height="${poster.height()}" fill="#E7E8E3"/>
</mask>
<g mask="url(#mask0_2594_2342)">
<g opacity="0.25" id="dots-deco-1" transform="translate(0, ${poster.shift()})">
    <circle cx="882" cy="905" r="5" transform="rotate(-90 882 905)" fill="white"/>
    <circle cx="882" cy="945" r="5" transform="rotate(-90 882 945)" fill="white"/>
    <circle cx="882" cy="825" r="10" transform="rotate(-90 882 825)" fill="white"/>
    <circle cx="882" cy="865" r="10" transform="rotate(-90 882 865)" fill="white"/>
    <circle cx="882" cy="745" r="5" transform="rotate(-90 882 745)" fill="white"/>
    <circle cx="882" cy="785" r="10" transform="rotate(-90 882 785)" fill="white"/>
    <circle cx="884" cy="675" r="10" transform="rotate(-90 884 675)" fill="white"/>
    <circle cx="882" cy="705" r="5" transform="rotate(-90 882 705)" fill="white"/>
    <circle cx="1204" cy="905" r="5" transform="rotate(-90 1204 905)" fill="white"/>
    <circle cx="1204" cy="945" r="10" transform="rotate(-90 1204 945)" fill="white"/>
    <circle cx="1204" cy="825" r="5" transform="rotate(-90 1204 825)" fill="white"/>
    <circle cx="1204" cy="865" r="5" transform="rotate(-90 1204 865)" fill="white"/>
    <circle cx="1209" cy="745" r="10" transform="rotate(-90 1209 745)" fill="white"/>
    <circle cx="1204" cy="785" r="5" transform="rotate(-90 1204 785)" fill="white"/>
    <circle cx="1204.5" cy="664.5" r="2.5" transform="rotate(-90 1204.5 664.5)" fill="white"/>
    <circle cx="1209" cy="706" r="10" transform="rotate(-90 1209 706)" fill="white"/>
    <circle cx="924.5" cy="905.5" r="2.5" transform="rotate(-90 924.5 905.5)" fill="white"/>
    <circle cx="924.5" cy="945.5" r="2.5" transform="rotate(-90 924.5 945.5)" fill="white"/>
    <circle cx="924.5" cy="825.5" r="2.5" transform="rotate(-90 924.5 825.5)" fill="white"/>
    <circle cx="924.5" cy="865.5" r="2.5" transform="rotate(-90 924.5 865.5)" fill="white"/>
    <circle cx="922" cy="750" r="10" transform="rotate(-90 922 750)" fill="white"/>
    <circle cx="924.5" cy="785.5" r="2.5" transform="rotate(-90 924.5 785.5)" fill="white"/>
    <circle cx="922" cy="675" r="10" transform="rotate(-90 922 675)" fill="white"/>
    <circle cx="922" cy="715" r="10" transform="rotate(-90 922 715)" fill="white"/>
    <circle cx="1244" cy="905" r="5" transform="rotate(-90 1244 905)" fill="white"/>
    <circle cx="1244" cy="945" r="10" transform="rotate(-90 1244 945)" fill="white"/>
    <circle cx="1244" cy="825" r="5" transform="rotate(-90 1244 825)" fill="white"/>
    <circle cx="1244" cy="865" r="5" transform="rotate(-90 1244 865)" fill="white"/>
    <circle cx="1249" cy="745" r="10" transform="rotate(-90 1249 745)" fill="white"/>
    <circle cx="1244" cy="785" r="5" transform="rotate(-90 1244 785)" fill="white"/>
    <circle cx="1244.5" cy="664.5" r="2.5" transform="rotate(-90 1244.5 664.5)" fill="white"/>
    <circle cx="1249" cy="706" r="10" transform="rotate(-90 1249 706)" fill="white"/>
    <circle cx="964" cy="905" r="15" transform="rotate(-90 964 905)" fill="white"/>
    <circle cx="964" cy="945" r="15" transform="rotate(-90 964 945)" fill="white"/>
    <circle cx="964" cy="825" r="15" transform="rotate(-90 964 825)" fill="white"/>
    <circle cx="964" cy="865" r="15" transform="rotate(-90 964 865)" fill="white"/>
    <circle cx="964" cy="750" r="5" transform="rotate(-90 964 750)" fill="white"/>
    <circle cx="964" cy="785" r="5" transform="rotate(-90 964 785)" fill="white"/>
    <circle cx="964" cy="675" r="5" transform="rotate(-90 964 675)" fill="white"/>
    <circle cx="964" cy="715" r="5" transform="rotate(-90 964 715)" fill="white"/>
    <circle cx="1284" cy="905" r="5" transform="rotate(-90 1284 905)" fill="white"/>
    <circle cx="1284" cy="945" r="10" transform="rotate(-90 1284 945)" fill="white"/>
    <circle cx="1284" cy="825" r="5" transform="rotate(-90 1284 825)" fill="white"/>
    <circle cx="1284" cy="865" r="5" transform="rotate(-90 1284 865)" fill="white"/>
    <circle cx="1289" cy="745" r="10" transform="rotate(-90 1289 745)" fill="white"/>
    <circle cx="1284" cy="785" r="5" transform="rotate(-90 1284 785)" fill="white"/>
    <circle cx="1287" cy="665" r="5" transform="rotate(-90 1287 665)" fill="white"/>
    <circle cx="1287" cy="705" r="5" transform="rotate(-90 1287 705)" fill="white"/>
    <circle cx="1002" cy="905" r="10" transform="rotate(-90 1002 905)" fill="white"/>
    <circle cx="1002" cy="945" r="10" transform="rotate(-90 1002 945)" fill="white"/>
    <circle cx="1002" cy="825" r="5" transform="rotate(-90 1002 825)" fill="white"/>
    <circle cx="1002" cy="865" r="5" transform="rotate(-90 1002 865)" fill="white"/>
    <circle cx="1002" cy="745" r="5" transform="rotate(-90 1002 745)" fill="white"/>
    <circle cx="1002" cy="785" r="5" transform="rotate(-90 1002 785)" fill="white"/>
    <circle cx="1002" cy="665" r="5" transform="rotate(-90 1002 665)" fill="white"/>
    <circle cx="1002" cy="705" r="5" transform="rotate(-90 1002 705)" fill="white"/>
    <circle cx="1324" cy="905" r="5" transform="rotate(-90 1324 905)" fill="white"/>
    <circle cx="1324" cy="945" r="10" transform="rotate(-90 1324 945)" fill="white"/>
    <circle cx="1324" cy="825" r="5" transform="rotate(-90 1324 825)" fill="white"/>
    <circle cx="1324" cy="865" r="5" transform="rotate(-90 1324 865)" fill="white"/>
    <circle cx="1324" cy="745" r="10" transform="rotate(-90 1324 745)" fill="white"/>
    <circle cx="1324" cy="785" r="5" transform="rotate(-90 1324 785)" fill="white"/>
    <circle cx="1324" cy="665" r="10" transform="rotate(-90 1324 665)" fill="white"/>
    <circle cx="1324" cy="705" r="10" transform="rotate(-90 1324 705)" fill="white"/>
    <circle cx="1044" cy="905" r="5" transform="rotate(-90 1044 905)" fill="white"/>
    <circle cx="1044" cy="946" r="15" transform="rotate(-90 1044 946)" fill="white"/>
    <circle cx="1044" cy="825" r="5" transform="rotate(-90 1044 825)" fill="white"/>
    <circle cx="1044" cy="865" r="5" transform="rotate(-90 1044 865)" fill="white"/>
    <circle cx="1042" cy="745" r="10" transform="rotate(-90 1042 745)" fill="white"/>
    <circle cx="1044" cy="785" r="5" transform="rotate(-90 1044 785)" fill="white"/>
    <circle cx="1042" cy="665" r="10" transform="rotate(-90 1042 665)" fill="white"/>
    <circle cx="1042" cy="705" r="10" transform="rotate(-90 1042 705)" fill="white"/>
    <circle cx="1364" cy="905" r="10" transform="rotate(-90 1364 905)" fill="white"/>
    <circle cx="1364" cy="945" r="10" transform="rotate(-90 1364 945)" fill="white"/>
    <circle cx="1364" cy="825" r="10" transform="rotate(-90 1364 825)" fill="white"/>
    <circle cx="1364" cy="865" r="10" transform="rotate(-90 1364 865)" fill="white"/>
    <circle cx="1364" cy="745" r="10" transform="rotate(-90 1364 745)" fill="white"/>
    <circle cx="1364" cy="785" r="10" transform="rotate(-90 1364 785)" fill="white"/>
    <circle cx="1364" cy="665" r="5" transform="rotate(-90 1364 665)" fill="white"/>
    <circle cx="1364" cy="705" r="10" transform="rotate(-90 1364 705)" fill="white"/>
    <circle cx="1084.5" cy="902.5" r="2.5" transform="rotate(-90 1084.5 902.5)" fill="white"/>
    <circle cx="1084.5" cy="945.5" r="2.5" transform="rotate(-90 1084.5 945.5)" fill="white"/>
    <circle cx="1084" cy="825" r="5" transform="rotate(-90 1084 825)" fill="white"/>
    <circle cx="1084.5" cy="862.5" r="2.5" transform="rotate(-90 1084.5 862.5)" fill="white"/>
    <circle cx="1083" cy="745" r="5" transform="rotate(-90 1083 745)" fill="white"/>
    <circle cx="1084" cy="785" r="5" transform="rotate(-90 1084 785)" fill="white"/>
    <circle cx="1084.5" cy="664.5" r="2.5" transform="rotate(-90 1084.5 664.5)" fill="white"/>
    <circle cx="1085" cy="705" r="15" transform="rotate(-90 1085 705)" fill="white"/>
    <circle cx="1404" cy="905" r="10" transform="rotate(-90 1404 905)" fill="white"/>
    <circle cx="1404" cy="945" r="10" transform="rotate(-90 1404 945)" fill="white"/>
    <circle cx="1404" cy="825" r="10" transform="rotate(-90 1404 825)" fill="white"/>
    <circle cx="1404" cy="865" r="10" transform="rotate(-90 1404 865)" fill="white"/>
    <circle cx="1403.5" cy="742.5" r="2.5" transform="rotate(-90 1403.5 742.5)" fill="white"/>
    <circle cx="1403.5" cy="782.5" r="2.5" transform="rotate(-90 1403.5 782.5)" fill="white"/>
    <circle cx="1403.5" cy="662.5" r="2.5" transform="rotate(-90 1403.5 662.5)" fill="white"/>
    <circle cx="1403.5" cy="702.5" r="2.5" transform="rotate(-90 1403.5 702.5)" fill="white"/>
    <circle cx="1124.5" cy="902.5" r="2.5" transform="rotate(-90 1124.5 902.5)" fill="white"/>
    <circle cx="1124.5" cy="945.5" r="2.5" transform="rotate(-90 1124.5 945.5)" fill="white"/>
    <circle cx="1124" cy="825" r="5" transform="rotate(-90 1124 825)" fill="white"/>
    <circle cx="1124.5" cy="862.5" r="2.5" transform="rotate(-90 1124.5 862.5)" fill="white"/>
    <circle cx="1123" cy="745" r="5" transform="rotate(-90 1123 745)" fill="white"/>
    <circle cx="1124" cy="785" r="5" transform="rotate(-90 1124 785)" fill="white"/>
    <circle cx="1124.5" cy="664.5" r="2.5" transform="rotate(-90 1124.5 664.5)" fill="white"/>
    <circle cx="1125" cy="705" r="15" transform="rotate(-90 1125 705)" fill="white"/>
    <circle cx="1447.5" cy="903.5" r="2.5" transform="rotate(-90 1447.5 903.5)" fill="white"/>
    <circle cx="1447.5" cy="943.5" r="2.5" transform="rotate(-90 1447.5 943.5)" fill="white"/>
    <circle cx="1447.5" cy="823.5" r="2.5" transform="rotate(-90 1447.5 823.5)" fill="white"/>
    <circle cx="1447.5" cy="863.5" r="2.5" transform="rotate(-90 1447.5 863.5)" fill="white"/>
    <circle cx="1446" cy="745" r="5" transform="rotate(-90 1446 745)" fill="white"/>
    <circle cx="1447.5" cy="783.5" r="2.5" transform="rotate(-90 1447.5 783.5)" fill="white"/>
    <circle cx="1446" cy="665" r="5" transform="rotate(-90 1446 665)" fill="white"/>
    <circle cx="1446" cy="705" r="5" transform="rotate(-90 1446 705)" fill="white"/>
    <circle cx="1164.5" cy="902.5" r="2.5" transform="rotate(-90 1164.5 902.5)" fill="white"/>
    <circle cx="1164.5" cy="945.5" r="2.5" transform="rotate(-90 1164.5 945.5)" fill="white"/>
    <circle cx="1164" cy="825" r="5" transform="rotate(-90 1164 825)" fill="white"/>
    <circle cx="1164.5" cy="862.5" r="2.5" transform="rotate(-90 1164.5 862.5)" fill="white"/>
    <circle cx="1163" cy="745" r="5" transform="rotate(-90 1163 745)" fill="white"/>
    <circle cx="1164" cy="785" r="5" transform="rotate(-90 1164 785)" fill="white"/>
    <circle cx="1164.5" cy="664.5" r="2.5" transform="rotate(-90 1164.5 664.5)" fill="white"/>
    <circle cx="1165" cy="705" r="15" transform="rotate(-90 1165 705)" fill="white"/>
    <circle cx="1487" cy="905" r="5" transform="rotate(-90 1487 905)" fill="white"/>
    <circle cx="1487" cy="945" r="5" transform="rotate(-90 1487 945)" fill="white"/>
    <circle cx="1486.5" cy="825.5" r="2.5" transform="rotate(-90 1486.5 825.5)" fill="white"/>
    <circle cx="1486.5" cy="865.5" r="2.5" transform="rotate(-90 1486.5 865.5)" fill="white"/>
    <circle cx="1486.5" cy="745.5" r="2.5" transform="rotate(-90 1486.5 745.5)" fill="white"/>
    <circle cx="1486.5" cy="785.5" r="2.5" transform="rotate(-90 1486.5 785.5)" fill="white"/>
    <circle cx="1486.5" cy="665.5" r="2.5" transform="rotate(-90 1486.5 665.5)" fill="white"/>
    <circle cx="1486.5" cy="705.5" r="2.5" transform="rotate(-90 1486.5 705.5)" fill="white"/>
    <circle cx="884" cy="595" r="10" transform="rotate(-90 884 595)" fill="white"/>
    <circle cx="884" cy="635" r="10" transform="rotate(-90 884 635)" fill="white"/>
    <circle cx="884" cy="515" r="10" transform="rotate(-90 884 515)" fill="white"/>
    <circle cx="884" cy="555" r="10" transform="rotate(-90 884 555)" fill="white"/>
    <circle cx="884" cy="435" r="10" transform="rotate(-90 884 435)" fill="white"/>
    <circle cx="884" cy="475" r="10" transform="rotate(-90 884 475)" fill="white"/>
    <circle cx="884" cy="355" r="10" transform="rotate(-90 884 355)" fill="white"/>
    <circle cx="884" cy="395" r="10" transform="rotate(-90 884 395)" fill="white"/>
    <circle cx="1207" cy="587" r="10" transform="rotate(-90 1207 587)" fill="white"/>
    <circle cx="1204.5" cy="624.5" r="2.5" transform="rotate(-90 1204.5 624.5)" fill="white"/>
    <circle cx="1206.5" cy="510.5" r="2.5" transform="rotate(-90 1206.5 510.5)" fill="white"/>
    <circle cx="1206.5" cy="550.5" r="2.5" transform="rotate(-90 1206.5 550.5)" fill="white"/>
    <circle cx="1204" cy="435" r="5" transform="rotate(-90 1204 435)" fill="white"/>
    <circle cx="1206.5" cy="470.5" r="2.5" transform="rotate(-90 1206.5 470.5)" fill="white"/>
    <circle cx="1204" cy="355" r="5" transform="rotate(-90 1204 355)" fill="white"/>
    <circle cx="1204" cy="395" r="5" transform="rotate(-90 1204 395)" fill="white"/>
    <circle cx="922" cy="595" r="5" transform="rotate(-90 922 595)" fill="white"/>
    <circle cx="922" cy="635" r="10" transform="rotate(-90 922 635)" fill="white"/>
    <circle cx="922" cy="515" r="5" transform="rotate(-90 922 515)" fill="white"/>
    <circle cx="922" cy="555" r="5" transform="rotate(-90 922 555)" fill="white"/>
    <circle cx="921.5" cy="435.5" r="2.5" transform="rotate(-90 921.5 435.5)" fill="white"/>
    <circle cx="922" cy="475" r="5" transform="rotate(-90 922 475)" fill="white"/>
    <circle cx="921.5" cy="355.5" r="2.5" transform="rotate(-90 921.5 355.5)" fill="white"/>
    <circle cx="921.5" cy="395.5" r="2.5" transform="rotate(-90 921.5 395.5)" fill="white"/>
    <circle cx="1247" cy="587" r="10" transform="rotate(-90 1247 587)" fill="white"/>
    <circle cx="1244.5" cy="624.5" r="2.5" transform="rotate(-90 1244.5 624.5)" fill="white"/>
    <circle cx="1246.5" cy="510.5" r="2.5" transform="rotate(-90 1246.5 510.5)" fill="white"/>
    <circle cx="1246.5" cy="550.5" r="2.5" transform="rotate(-90 1246.5 550.5)" fill="white"/>
    <circle cx="1244" cy="435" r="5" transform="rotate(-90 1244 435)" fill="white"/>
    <circle cx="1246.5" cy="470.5" r="2.5" transform="rotate(-90 1246.5 470.5)" fill="white"/>
    <circle cx="1244" cy="355" r="5" transform="rotate(-90 1244 355)" fill="white"/>
    <circle cx="1244" cy="395" r="5" transform="rotate(-90 1244 395)" fill="white"/>
    <circle cx="964" cy="595" r="5" transform="rotate(-90 964 595)" fill="white"/>
    <circle cx="964" cy="635" r="5" transform="rotate(-90 964 635)" fill="white"/>
    <circle cx="964" cy="515" r="10" transform="rotate(-90 964 515)" fill="white"/>
    <circle cx="964" cy="555" r="10" transform="rotate(-90 964 555)" fill="white"/>
    <circle cx="963.5" cy="434.5" r="2.5" transform="rotate(-90 963.5 434.5)" fill="white"/>
    <circle cx="964" cy="475" r="10" transform="rotate(-90 964 475)" fill="white"/>
    <circle cx="963.5" cy="354.5" r="2.5" transform="rotate(-90 963.5 354.5)" fill="white"/>
    <circle cx="963.5" cy="394.5" r="2.5" transform="rotate(-90 963.5 394.5)" fill="white"/>
    <circle cx="1286.5" cy="590.5" r="2.5" transform="rotate(-90 1286.5 590.5)" fill="white"/>
    <circle cx="1287" cy="625" r="5" transform="rotate(-90 1287 625)" fill="white"/>
    <circle cx="1286.5" cy="510.5" r="2.5" transform="rotate(-90 1286.5 510.5)" fill="white"/>
    <circle cx="1286.5" cy="550.5" r="2.5" transform="rotate(-90 1286.5 550.5)" fill="white"/>
    <circle cx="1281.5" cy="435.5" r="2.5" transform="rotate(-90 1281.5 435.5)" fill="white"/>
    <circle cx="1286.5" cy="470.5" r="2.5" transform="rotate(-90 1286.5 470.5)" fill="white"/>
    <circle cx="1281.5" cy="355.5" r="2.5" transform="rotate(-90 1281.5 355.5)" fill="white"/>
    <circle cx="1281.5" cy="395.5" r="2.5" transform="rotate(-90 1281.5 395.5)" fill="white"/>
    <circle cx="1002" cy="585" r="5" transform="rotate(-90 1002 585)" fill="white"/>
    <circle cx="1002" cy="625" r="5" transform="rotate(-90 1002 625)" fill="white"/>
    <circle cx="1002" cy="505" r="5" transform="rotate(-90 1002 505)" fill="white"/>
    <circle cx="1002" cy="545" r="5" transform="rotate(-90 1002 545)" fill="white"/>
    <circle cx="1003.5" cy="434.5" r="2.5" transform="rotate(-90 1003.5 434.5)" fill="white"/>
    <circle cx="1004" cy="475" r="10" transform="rotate(-90 1004 475)" fill="white"/>
    <circle cx="1003.5" cy="354.5" r="2.5" transform="rotate(-90 1003.5 354.5)" fill="white"/>
    <circle cx="1003.5" cy="394.5" r="2.5" transform="rotate(-90 1003.5 394.5)" fill="white"/>
    <circle cx="1324" cy="585" r="10" transform="rotate(-90 1324 585)" fill="white"/>
    <circle cx="1324" cy="625" r="10" transform="rotate(-90 1324 625)" fill="white"/>
    <circle cx="1324" cy="505" r="15" transform="rotate(-90 1324 505)" fill="white"/>
    <circle cx="1324" cy="545" r="15" transform="rotate(-90 1324 545)" fill="white"/>
    <circle cx="1321.5" cy="435.5" r="2.5" transform="rotate(-90 1321.5 435.5)" fill="white"/>
    <circle cx="1324" cy="465" r="15" transform="rotate(-90 1324 465)" fill="white"/>
    <circle cx="1321.5" cy="355.5" r="2.5" transform="rotate(-90 1321.5 355.5)" fill="white"/>
    <circle cx="1321.5" cy="395.5" r="2.5" transform="rotate(-90 1321.5 395.5)" fill="white"/>
    <circle cx="1042" cy="585" r="10" transform="rotate(-90 1042 585)" fill="white"/>
    <circle cx="1042" cy="625" r="10" transform="rotate(-90 1042 625)" fill="white"/>
    <circle cx="1043.5" cy="507.5" r="2.5" transform="rotate(-90 1043.5 507.5)" fill="white"/>
    <circle cx="1043.5" cy="547.5" r="2.5" transform="rotate(-90 1043.5 547.5)" fill="white"/>
    <circle cx="1043.5" cy="434.5" r="2.5" transform="rotate(-90 1043.5 434.5)" fill="white"/>
    <circle cx="1044" cy="475" r="10" transform="rotate(-90 1044 475)" fill="white"/>
    <circle cx="1043.5" cy="354.5" r="2.5" transform="rotate(-90 1043.5 354.5)" fill="white"/>
    <circle cx="1043.5" cy="394.5" r="2.5" transform="rotate(-90 1043.5 394.5)" fill="white"/>
    <circle cx="1364" cy="585" r="5" transform="rotate(-90 1364 585)" fill="white"/>
    <circle cx="1364" cy="625" r="5" transform="rotate(-90 1364 625)" fill="white"/>
    <circle cx="1364" cy="505" r="15" transform="rotate(-90 1364 505)" fill="white"/>
    <circle cx="1364" cy="545" r="15" transform="rotate(-90 1364 545)" fill="white"/>
    <circle cx="1361.5" cy="435.5" r="2.5" transform="rotate(-90 1361.5 435.5)" fill="white"/>
    <circle cx="1364" cy="465" r="15" transform="rotate(-90 1364 465)" fill="white"/>
    <circle cx="1361.5" cy="355.5" r="2.5" transform="rotate(-90 1361.5 355.5)" fill="white"/>
    <circle cx="1361.5" cy="395.5" r="2.5" transform="rotate(-90 1361.5 395.5)" fill="white"/>
    <circle cx="1087" cy="587" r="10" transform="rotate(-90 1087 587)" fill="white"/>
    <circle cx="1084.5" cy="624.5" r="2.5" transform="rotate(-90 1084.5 624.5)" fill="white"/>
    <circle cx="1086.5" cy="510.5" r="2.5" transform="rotate(-90 1086.5 510.5)" fill="white"/>
    <circle cx="1086.5" cy="550.5" r="2.5" transform="rotate(-90 1086.5 550.5)" fill="white"/>
    <circle cx="1084" cy="435" r="5" transform="rotate(-90 1084 435)" fill="white"/>
    <circle cx="1086.5" cy="470.5" r="2.5" transform="rotate(-90 1086.5 470.5)" fill="white"/>
    <circle cx="1084" cy="355" r="5" transform="rotate(-90 1084 355)" fill="white"/>
    <circle cx="1084" cy="395" r="5" transform="rotate(-90 1084 395)" fill="white"/>
    <circle cx="1404" cy="585" r="10" transform="rotate(-90 1404 585)" fill="white"/>
    <circle cx="1403.5" cy="622.5" r="2.5" transform="rotate(-90 1403.5 622.5)" fill="white"/>
    <circle cx="1404" cy="505" r="10" transform="rotate(-90 1404 505)" fill="white"/>
    <circle cx="1404" cy="545" r="10" transform="rotate(-90 1404 545)" fill="white"/>
    <circle cx="1401.5" cy="435.5" r="2.5" transform="rotate(-90 1401.5 435.5)" fill="white"/>
    <circle cx="1404" cy="465" r="10" transform="rotate(-90 1404 465)" fill="white"/>
    <circle cx="1401.5" cy="355.5" r="2.5" transform="rotate(-90 1401.5 355.5)" fill="white"/>
    <circle cx="1401.5" cy="395.5" r="2.5" transform="rotate(-90 1401.5 395.5)" fill="white"/>
    <circle cx="1127" cy="587" r="10" transform="rotate(-90 1127 587)" fill="white"/>
    <circle cx="1124.5" cy="624.5" r="2.5" transform="rotate(-90 1124.5 624.5)" fill="white"/>
    <circle cx="1126.5" cy="510.5" r="2.5" transform="rotate(-90 1126.5 510.5)" fill="white"/>
    <circle cx="1126.5" cy="550.5" r="2.5" transform="rotate(-90 1126.5 550.5)" fill="white"/>
    <circle cx="1124" cy="435" r="5" transform="rotate(-90 1124 435)" fill="white"/>
    <circle cx="1126.5" cy="470.5" r="2.5" transform="rotate(-90 1126.5 470.5)" fill="white"/>
    <circle cx="1124" cy="355" r="5" transform="rotate(-90 1124 355)" fill="white"/>
    <circle cx="1124" cy="395" r="5" transform="rotate(-90 1124 395)" fill="white"/>
    <circle cx="1446" cy="585" r="5" transform="rotate(-90 1446 585)" fill="white"/>
    <circle cx="1446" cy="625" r="5" transform="rotate(-90 1446 625)" fill="white"/>
    <circle cx="1446" cy="505" r="5" transform="rotate(-90 1446 505)" fill="white"/>
    <circle cx="1446" cy="545" r="5" transform="rotate(-90 1446 545)" fill="white"/>
    <circle cx="1441.5" cy="435.5" r="2.5" transform="rotate(-90 1441.5 435.5)" fill="white"/>
    <circle cx="1446" cy="465" r="5" transform="rotate(-90 1446 465)" fill="white"/>
    <circle cx="1441.5" cy="355.5" r="2.5" transform="rotate(-90 1441.5 355.5)" fill="white"/>
    <circle cx="1441.5" cy="395.5" r="2.5" transform="rotate(-90 1441.5 395.5)" fill="white"/>
    <circle cx="1167" cy="587" r="10" transform="rotate(-90 1167 587)" fill="white"/>
    <circle cx="1164.5" cy="624.5" r="2.5" transform="rotate(-90 1164.5 624.5)" fill="white"/>
    <circle cx="1166.5" cy="510.5" r="2.5" transform="rotate(-90 1166.5 510.5)" fill="white"/>
    <circle cx="1166.5" cy="550.5" r="2.5" transform="rotate(-90 1166.5 550.5)" fill="white"/>
    <circle cx="1164" cy="435" r="5" transform="rotate(-90 1164 435)" fill="white"/>
    <circle cx="1166.5" cy="470.5" r="2.5" transform="rotate(-90 1166.5 470.5)" fill="white"/>
    <circle cx="1164" cy="355" r="5" transform="rotate(-90 1164 355)" fill="white"/>
    <circle cx="1164" cy="395" r="5" transform="rotate(-90 1164 395)" fill="white"/>
    <circle cx="1484" cy="585" r="10" transform="rotate(-90 1484 585)" fill="white"/>
    <circle cx="1484" cy="625" r="10" transform="rotate(-90 1484 625)" fill="white"/>
    <circle cx="1484" cy="505" r="10" transform="rotate(-90 1484 505)" fill="white"/>
    <circle cx="1484" cy="545" r="10" transform="rotate(-90 1484 545)" fill="white"/>
    <circle cx="882" cy="1345" r="5" transform="rotate(-90 882 1345)" fill="white"/>
    <circle cx="882" cy="1385" r="5" transform="rotate(-90 882 1385)" fill="white"/>
    <circle cx="882" cy="1265" r="5" transform="rotate(-90 882 1265)" fill="white"/>
    <circle cx="882" cy="1305" r="5" transform="rotate(-90 882 1305)" fill="white"/>
    <circle cx="881.5" cy="1185.5" r="2.5" transform="rotate(-90 881.5 1185.5)" fill="white"/>
    <circle cx="881.5" cy="1225.5" r="2.5" transform="rotate(-90 881.5 1225.5)" fill="white"/>
    <circle cx="881.5" cy="1105.5" r="2.5" transform="rotate(-90 881.5 1105.5)" fill="white"/>
    <circle cx="881.5" cy="1145.5" r="2.5" transform="rotate(-90 881.5 1145.5)" fill="white"/>
    <circle cx="1202" cy="1346" r="5" transform="rotate(-90 1202 1346)" fill="white"/>
    <circle cx="1202" cy="1386" r="5" transform="rotate(-90 1202 1386)" fill="white"/>
    <circle cx="1204" cy="1265" r="5" transform="rotate(-90 1204 1265)" fill="white"/>
    <circle cx="1204" cy="1305" r="5" transform="rotate(-90 1204 1305)" fill="white"/>
    <circle cx="1204" cy="1185" r="5" transform="rotate(-90 1204 1185)" fill="white"/>
    <circle cx="1204" cy="1225" r="5" transform="rotate(-90 1204 1225)" fill="white"/>
    <circle cx="1204" cy="1105" r="5" transform="rotate(-90 1204 1105)" fill="white"/>
    <circle cx="1204" cy="1145" r="5" transform="rotate(-90 1204 1145)" fill="white"/>
    <circle cx="924" cy="1345" r="10" transform="rotate(-90 924 1345)" fill="white"/>
    <circle cx="924" cy="1385" r="10" transform="rotate(-90 924 1385)" fill="white"/>
    <circle cx="924" cy="1265" r="5" transform="rotate(-90 924 1265)" fill="white"/>
    <circle cx="924" cy="1305" r="10" transform="rotate(-90 924 1305)" fill="white"/>
    <circle cx="924" cy="1185" r="5" transform="rotate(-90 924 1185)" fill="white"/>
    <circle cx="924" cy="1225" r="5" transform="rotate(-90 924 1225)" fill="white"/>
    <circle cx="924" cy="1105" r="5" transform="rotate(-90 924 1105)" fill="white"/>
    <circle cx="924" cy="1145" r="5" transform="rotate(-90 924 1145)" fill="white"/>
    <circle cx="1242" cy="1346" r="5" transform="rotate(-90 1242 1346)" fill="white"/>
    <circle cx="1242" cy="1386" r="5" transform="rotate(-90 1242 1386)" fill="white"/>
    <circle cx="1244" cy="1266" r="15" transform="rotate(-90 1244 1266)" fill="white"/>
    <circle cx="1244" cy="1306" r="15" transform="rotate(-90 1244 1306)" fill="white"/>
    <circle cx="1244" cy="1185" r="5" transform="rotate(-90 1244 1185)" fill="white"/>
    <circle cx="1244" cy="1225" r="5" transform="rotate(-90 1244 1225)" fill="white"/>
    <circle cx="1244" cy="1105" r="10" transform="rotate(-90 1244 1105)" fill="white"/>
    <circle cx="1244" cy="1145" r="10" transform="rotate(-90 1244 1145)" fill="white"/>
    <circle cx="963.5" cy="1344.5" r="2.5" transform="rotate(-90 963.5 1344.5)" fill="white"/>
    <circle cx="964" cy="1385" r="15" transform="rotate(-90 964 1385)" fill="white"/>
    <circle cx="964" cy="1265" r="5" transform="rotate(-90 964 1265)" fill="white"/>
    <circle cx="963.5" cy="1304.5" r="2.5" transform="rotate(-90 963.5 1304.5)" fill="white"/>
    <circle cx="964" cy="1185" r="5" transform="rotate(-90 964 1185)" fill="white"/>
    <circle cx="964" cy="1225" r="5" transform="rotate(-90 964 1225)" fill="white"/>
    <circle cx="964" cy="1105" r="5" transform="rotate(-90 964 1105)" fill="white"/>
    <circle cx="964" cy="1145" r="5" transform="rotate(-90 964 1145)" fill="white"/>
    <circle cx="1284" cy="1348" r="5" transform="rotate(-90 1284 1348)" fill="white"/>
    <circle cx="1284" cy="1388" r="5" transform="rotate(-90 1284 1388)" fill="white"/>
    <circle cx="1284" cy="1266" r="15" transform="rotate(-90 1284 1266)" fill="white"/>
    <circle cx="1284" cy="1308" r="5" transform="rotate(-90 1284 1308)" fill="white"/>
    <circle cx="1284" cy="1185" r="5" transform="rotate(-90 1284 1185)" fill="white"/>
    <circle cx="1284" cy="1225" r="5" transform="rotate(-90 1284 1225)" fill="white"/>
    <circle cx="1284" cy="1107" r="5" transform="rotate(-90 1284 1107)" fill="white"/>
    <circle cx="1284" cy="1146" r="15" transform="rotate(-90 1284 1146)" fill="white"/>
    <circle cx="1003.5" cy="1344.5" r="2.5" transform="rotate(-90 1003.5 1344.5)" fill="white"/>
    <circle cx="1004" cy="1385" r="15" transform="rotate(-90 1004 1385)" fill="white"/>
    <circle cx="1003" cy="1265" r="5" transform="rotate(-90 1003 1265)" fill="white"/>
    <circle cx="1003.5" cy="1304.5" r="2.5" transform="rotate(-90 1003.5 1304.5)" fill="white"/>
    <circle cx="1004" cy="1185" r="5" transform="rotate(-90 1004 1185)" fill="white"/>
    <circle cx="1004" cy="1225" r="5" transform="rotate(-90 1004 1225)" fill="white"/>
    <circle cx="1002" cy="1105" r="10" transform="rotate(-90 1002 1105)" fill="white"/>
    <circle cx="1002" cy="1145" r="10" transform="rotate(-90 1002 1145)" fill="white"/>
    <circle cx="1324" cy="1348" r="5" transform="rotate(-90 1324 1348)" fill="white"/>
    <circle cx="1324" cy="1388" r="5" transform="rotate(-90 1324 1388)" fill="white"/>
    <circle cx="1324" cy="1266" r="15" transform="rotate(-90 1324 1266)" fill="white"/>
    <circle cx="1324" cy="1308" r="5" transform="rotate(-90 1324 1308)" fill="white"/>
    <circle cx="1324" cy="1185" r="5" transform="rotate(-90 1324 1185)" fill="white"/>
    <circle cx="1324" cy="1225" r="5" transform="rotate(-90 1324 1225)" fill="white"/>
    <circle cx="1324" cy="1107" r="5" transform="rotate(-90 1324 1107)" fill="white"/>
    <circle cx="1324" cy="1146" r="15" transform="rotate(-90 1324 1146)" fill="white"/>
    <circle cx="1043.5" cy="1344.5" r="2.5" transform="rotate(-90 1043.5 1344.5)" fill="white"/>
    <circle cx="1043.5" cy="1384.5" r="2.5" transform="rotate(-90 1043.5 1384.5)" fill="white"/>
    <circle cx="1043" cy="1265" r="5" transform="rotate(-90 1043 1265)" fill="white"/>
    <circle cx="1043.5" cy="1304.5" r="2.5" transform="rotate(-90 1043.5 1304.5)" fill="white"/>
    <circle cx="1044" cy="1185" r="5" transform="rotate(-90 1044 1185)" fill="white"/>
    <circle cx="1044" cy="1225" r="5" transform="rotate(-90 1044 1225)" fill="white"/>
    <circle cx="1043" cy="1105" r="10" transform="rotate(-90 1043 1105)" fill="white"/>
    <circle cx="1043" cy="1145" r="10" transform="rotate(-90 1043 1145)" fill="white"/>
    <circle cx="1364" cy="1348" r="5" transform="rotate(-90 1364 1348)" fill="white"/>
    <circle cx="1364" cy="1388" r="5" transform="rotate(-90 1364 1388)" fill="white"/>
    <circle cx="1364" cy="1266" r="15" transform="rotate(-90 1364 1266)" fill="white"/>
    <circle cx="1364" cy="1308" r="5" transform="rotate(-90 1364 1308)" fill="white"/>
    <circle cx="1364" cy="1185" r="5" transform="rotate(-90 1364 1185)" fill="white"/>
    <circle cx="1364" cy="1225" r="5" transform="rotate(-90 1364 1225)" fill="white"/>
    <circle cx="1364" cy="1107" r="5" transform="rotate(-90 1364 1107)" fill="white"/>
    <circle cx="1364" cy="1146" r="15" transform="rotate(-90 1364 1146)" fill="white"/>
    <circle cx="1083" cy="1343" r="10" transform="rotate(-90 1083 1343)" fill="white"/>
    <circle cx="1083.5" cy="1384.5" r="2.5" transform="rotate(-90 1083.5 1384.5)" fill="white"/>
    <circle cx="1083" cy="1265" r="5" transform="rotate(-90 1083 1265)" fill="white"/>
    <circle cx="1083" cy="1303" r="10" transform="rotate(-90 1083 1303)" fill="white"/>
    <circle cx="1084" cy="1185" r="5" transform="rotate(-90 1084 1185)" fill="white"/>
    <circle cx="1084" cy="1225" r="5" transform="rotate(-90 1084 1225)" fill="white"/>
    <circle cx="1083" cy="1105" r="10" transform="rotate(-90 1083 1105)" fill="white"/>
    <circle cx="1083" cy="1145" r="10" transform="rotate(-90 1083 1145)" fill="white"/>
    <circle cx="1406.5" cy="1347.5" r="2.5" transform="matrix(0 -1 1 0 59 2754)" fill="white"/>
    <circle cx="1406.5" cy="1387.5" r="2.5" transform="matrix(0 -1 1 0 19 2794)" fill="white"/>
    <circle cx="1406.5" cy="1267.5" r="2.5" transform="rotate(-90 1406.5 1267.5)" fill="white"/>
    <circle cx="1406.5" cy="1307.5" r="2.5" transform="matrix(0 -1 1 0 99 2714)" fill="white"/>
    <circle cx="1406.5" cy="1187.5" r="2.5" transform="rotate(-90 1406.5 1187.5)" fill="white"/>
    <circle cx="1406.5" cy="1227.5" r="2.5" transform="rotate(-90 1406.5 1227.5)" fill="white"/>
    <circle cx="1404" cy="1107" r="5" transform="rotate(-90 1404 1107)" fill="white"/>
    <circle cx="1406.5" cy="1147.5" r="2.5" transform="rotate(-90 1406.5 1147.5)" fill="white"/>
    <circle cx="1123" cy="1345" r="10" transform="rotate(-90 1123 1345)" fill="white"/>
    <circle cx="1123" cy="1385" r="10" transform="rotate(-90 1123 1385)" fill="white"/>
    <circle cx="1124" cy="1265" r="5" transform="rotate(-90 1124 1265)" fill="white"/>
    <circle cx="1124" cy="1305" r="5" transform="rotate(-90 1124 1305)" fill="white"/>
    <circle cx="1124" cy="1185" r="5" transform="rotate(-90 1124 1185)" fill="white"/>
    <circle cx="1124" cy="1225" r="5" transform="rotate(-90 1124 1225)" fill="white"/>
    <circle cx="1124" cy="1105" r="5" transform="rotate(-90 1124 1105)" fill="white"/>
    <circle cx="1124" cy="1145" r="5" transform="rotate(-90 1124 1145)" fill="white"/>
    <circle cx="1446.5" cy="1347.5" r="2.5" transform="matrix(0 -1 1 0 99 2794)" fill="white"/>
    <circle cx="1446.5" cy="1387.5" r="2.5" transform="matrix(0 -1 1 0 59 2834)" fill="white"/>
    <circle cx="1446.5" cy="1267.5" r="2.5" transform="rotate(-90 1446.5 1267.5)" fill="white"/>
    <circle cx="1446.5" cy="1307.5" r="2.5" transform="rotate(-90 1446.5 1307.5)" fill="white"/>
    <circle cx="1446.5" cy="1187.5" r="2.5" transform="rotate(-90 1446.5 1187.5)" fill="white"/>
    <circle cx="1446.5" cy="1227.5" r="2.5" transform="rotate(-90 1446.5 1227.5)" fill="white"/>
    <circle cx="1447" cy="1107" r="10" transform="rotate(-90 1447 1107)" fill="white"/>
    <circle cx="1446.5" cy="1147.5" r="2.5" transform="rotate(-90 1446.5 1147.5)" fill="white"/>
    <circle cx="1162" cy="1346" r="5" transform="rotate(-90 1162 1346)" fill="white"/>
    <circle cx="1162" cy="1386" r="5" transform="rotate(-90 1162 1386)" fill="white"/>
    <circle cx="1164" cy="1265" r="5" transform="rotate(-90 1164 1265)" fill="white"/>
    <circle cx="1164" cy="1305" r="5" transform="rotate(-90 1164 1305)" fill="white"/>
    <circle cx="1164" cy="1185" r="5" transform="rotate(-90 1164 1185)" fill="white"/>
    <circle cx="1164" cy="1225" r="5" transform="rotate(-90 1164 1225)" fill="white"/>
    <circle cx="1164" cy="1105" r="5" transform="rotate(-90 1164 1105)" fill="white"/>
    <circle cx="1164" cy="1145" r="5" transform="rotate(-90 1164 1145)" fill="white"/>
    <circle cx="1486.5" cy="1347.5" r="2.5" transform="rotate(-90 1486.5 1347.5)" fill="white"/>
    <circle cx="1486.5" cy="1387.5" r="2.5" transform="matrix(0 -1 1 0 99 2874)" fill="white"/>
    <circle cx="1486.5" cy="1267.5" r="2.5" transform="rotate(-90 1486.5 1267.5)" fill="white"/>
    <circle cx="1486.5" cy="1307.5" r="2.5" transform="rotate(-90 1486.5 1307.5)" fill="white"/>
    <circle cx="882" cy="1465" r="5" transform="rotate(-90 882 1465)" fill="white"/>
    <circle cx="882" cy="1505" r="5" transform="rotate(-90 882 1505)" fill="white"/>
    <circle cx="882" cy="1425" r="5" transform="rotate(-90 882 1425)" fill="white"/>
    <circle cx="1204" cy="1465" r="5" transform="rotate(-90 1204 1465)" fill="white"/>
    <circle cx="1204.5" cy="1504.5" r="2.5" transform="rotate(-90 1204.5 1504.5)" fill="white"/>
    <circle cx="1202" cy="1426" r="5" transform="rotate(-90 1202 1426)" fill="white"/>
    <circle cx="924" cy="1465" r="10" transform="rotate(-90 924 1465)" fill="white"/>
    <circle cx="924" cy="1505" r="10" transform="rotate(-90 924 1505)" fill="white"/>
    <circle cx="924" cy="1425" r="10" transform="rotate(-90 924 1425)" fill="white"/>
    <circle cx="1244" cy="1465" r="5" transform="rotate(-90 1244 1465)" fill="white"/>
    <circle cx="1244.5" cy="1504.5" r="2.5" transform="rotate(-90 1244.5 1504.5)" fill="white"/>
    <circle cx="1242" cy="1426" r="5" transform="rotate(-90 1242 1426)" fill="white"/>
    <circle cx="964" cy="1465" r="15" transform="rotate(-90 964 1465)" fill="white"/>
    <circle cx="964" cy="1505" r="10" transform="rotate(-90 964 1505)" fill="white"/>
    <circle cx="964" cy="1425" r="15" transform="rotate(-90 964 1425)" fill="white"/>
    <circle cx="1286" cy="1465" r="10" transform="rotate(-90 1286 1465)" fill="white"/>
    <circle cx="1286" cy="1505" r="10" transform="rotate(-90 1286 1505)" fill="white"/>
    <circle cx="1284" cy="1426" r="15" transform="rotate(-90 1284 1426)" fill="white"/>
    <circle cx="1004" cy="1465" r="15" transform="rotate(-90 1004 1465)" fill="white"/>
    <circle cx="1004" cy="1505" r="10" transform="rotate(-90 1004 1505)" fill="white"/>
    <circle cx="1004" cy="1425" r="15" transform="rotate(-90 1004 1425)" fill="white"/>
    <circle cx="1326" cy="1465" r="10" transform="rotate(-90 1326 1465)" fill="white"/>
    <circle cx="1326" cy="1505" r="10" transform="rotate(-90 1326 1505)" fill="white"/>
    <circle cx="1324" cy="1426" r="15" transform="rotate(-90 1324 1426)" fill="white"/>
    <circle cx="1043.5" cy="1464.5" r="2.5" transform="rotate(-90 1043.5 1464.5)" fill="white"/>
    <circle cx="1044" cy="1505" r="5" transform="rotate(-90 1044 1505)" fill="white"/>
    <circle cx="1043.5" cy="1424.5" r="2.5" transform="rotate(-90 1043.5 1424.5)" fill="white"/>
    <circle cx="1366" cy="1465" r="10" transform="rotate(-90 1366 1465)" fill="white"/>
    <circle cx="1366" cy="1505" r="10" transform="rotate(-90 1366 1505)" fill="white"/>
    <circle cx="1364" cy="1426" r="15" transform="rotate(-90 1364 1426)" fill="white"/>
    <circle cx="1083.5" cy="1464.5" r="2.5" transform="rotate(-90 1083.5 1464.5)" fill="white"/>
    <circle cx="1083.5" cy="1504.5" r="2.5" transform="rotate(-90 1083.5 1504.5)" fill="white"/>
    <circle cx="1083.5" cy="1424.5" r="2.5" transform="rotate(-90 1083.5 1424.5)" fill="white"/>
    <circle cx="1406" cy="1465" r="10" transform="rotate(-90 1406 1465)" fill="white"/>
    <circle cx="1406" cy="1505" r="10" transform="rotate(-90 1406 1505)" fill="white"/>
    <circle cx="1406.5" cy="1427.5" r="2.5" transform="rotate(-90 1406.5 1427.5)" fill="white"/>
    <circle cx="1123" cy="1465" r="10" transform="rotate(-90 1123 1465)" fill="white"/>
    <circle cx="1123" cy="1505" r="10" transform="rotate(-90 1123 1505)" fill="white"/>
    <circle cx="1123" cy="1425" r="10" transform="rotate(-90 1123 1425)" fill="white"/>
    <circle cx="1446" cy="1465" r="10" transform="rotate(-90 1446 1465)" fill="white"/>
    <circle cx="1446" cy="1505" r="10" transform="rotate(-90 1446 1505)" fill="white"/>
    <circle cx="1446.5" cy="1427.5" r="2.5" transform="matrix(0 -1 1 0 19 2874)" fill="white"/>
    <circle cx="1164" cy="1465" r="5" transform="rotate(-90 1164 1465)" fill="white"/>
    <circle cx="1164.5" cy="1504.5" r="2.5" transform="rotate(-90 1164.5 1504.5)" fill="white"/>
    <circle cx="1162" cy="1426" r="5" transform="rotate(-90 1162 1426)" fill="white"/>
    <circle cx="1484" cy="1465" r="10" transform="rotate(-90 1484 1465)" fill="white"/>
    <circle cx="1484" cy="1505" r="10" transform="rotate(-90 1484 1505)" fill="white"/>
    <circle cx="1204.5" cy="1544.5" r="2.5" transform="rotate(-90 1204.5 1544.5)" fill="white"/>
    <circle cx="1204" cy="1585" r="5" transform="rotate(-90 1204 1585)" fill="white"/>
    <circle cx="923.5" cy="1544.5" r="2.5" transform="rotate(-90 923.5 1544.5)" fill="white"/>
    <circle cx="923.5" cy="1584.5" r="2.5" transform="rotate(-90 923.5 1584.5)" fill="white"/>
    <circle cx="1244.5" cy="1544.5" r="2.5" transform="rotate(-90 1244.5 1544.5)" fill="white"/>
    <circle cx="1244" cy="1585" r="5" transform="rotate(-90 1244 1585)" fill="white"/>
    <circle cx="964" cy="1545" r="10" transform="rotate(-90 964 1545)" fill="white"/>
    <circle cx="964" cy="1585" r="10" transform="rotate(-90 964 1585)" fill="white"/>
    <circle cx="1284" cy="1545" r="5" transform="rotate(-90 1284 1545)" fill="white"/>
    <circle cx="1284" cy="1585" r="5" transform="rotate(-90 1284 1585)" fill="white"/>
    <circle cx="1004" cy="1545" r="10" transform="rotate(-90 1004 1545)" fill="white"/>
    <circle cx="1004" cy="1585" r="10" transform="rotate(-90 1004 1585)" fill="white"/>
    <circle cx="1324" cy="1546" r="15" transform="rotate(-90 1324 1546)" fill="white"/>
    <circle cx="1324" cy="1586" r="15" transform="rotate(-90 1324 1586)" fill="white"/>
    <circle cx="1044" cy="1545" r="5" transform="rotate(-90 1044 1545)" fill="white"/>
    <circle cx="1044" cy="1585" r="5" transform="rotate(-90 1044 1585)" fill="white"/>
    <circle cx="1364.5" cy="1544.5" r="2.5" transform="rotate(-90 1364.5 1544.5)" fill="white"/>
    <circle cx="1364.5" cy="1584.5" r="2.5" transform="rotate(-90 1364.5 1584.5)" fill="white"/>
    <circle cx="1083.5" cy="1544.5" r="2.5" transform="rotate(-90 1083.5 1544.5)" fill="white"/>
    <circle cx="1084" cy="1585" r="10" transform="rotate(-90 1084 1585)" fill="white"/>
    <circle cx="1404.5" cy="1544.5" r="2.5" transform="rotate(-90 1404.5 1544.5)" fill="white"/>
    <circle cx="1404.5" cy="1584.5" r="2.5" transform="rotate(-90 1404.5 1584.5)" fill="white"/>
    <circle cx="1124" cy="1545" r="10" transform="rotate(-90 1124 1545)" fill="white"/>
    <circle cx="1124" cy="1585" r="10" transform="rotate(-90 1124 1585)" fill="white"/>
    <circle cx="1444.5" cy="1544.5" r="2.5" transform="rotate(-90 1444.5 1544.5)" fill="white"/>
    <circle cx="1444.5" cy="1584.5" r="2.5" transform="rotate(-90 1444.5 1584.5)" fill="white"/>
    <circle cx="1164.5" cy="1544.5" r="2.5" transform="rotate(-90 1164.5 1544.5)" fill="white"/>
    <circle cx="1164" cy="1585" r="5" transform="rotate(-90 1164 1585)" fill="white"/>
    <circle cx="1484" cy="1545" r="10" transform="rotate(-90 1484 1545)" fill="white"/>
    <circle cx="1484" cy="1585" r="5" transform="rotate(-90 1484 1585)" fill="white"/>
    <circle cx="1204" cy="1627" r="5" transform="rotate(-90 1204 1627)" fill="white"/>
    <circle cx="1244" cy="1627" r="5" transform="rotate(-90 1244 1627)" fill="white"/>
    <circle cx="964" cy="1625" r="10" transform="rotate(-90 964 1625)" fill="white"/>
    <circle cx="1284" cy="1627" r="5" transform="rotate(-90 1284 1627)" fill="white"/>
    <circle cx="1004" cy="1625" r="10" transform="rotate(-90 1004 1625)" fill="white"/>
    <circle cx="1324" cy="1627" r="5" transform="rotate(-90 1324 1627)" fill="white"/>
    <circle cx="1044" cy="1625" r="5" transform="rotate(-90 1044 1625)" fill="white"/>
    <circle cx="1364.5" cy="1624.5" r="2.5" transform="rotate(-90 1364.5 1624.5)" fill="white"/>
    <circle cx="1084" cy="1625" r="10" transform="rotate(-90 1084 1625)" fill="white"/>
    <circle cx="1404.5" cy="1624.5" r="2.5" transform="rotate(-90 1404.5 1624.5)" fill="white"/>
    <circle cx="1124" cy="1627" r="5" transform="rotate(-90 1124 1627)" fill="white"/>
    <circle cx="1444.5" cy="1624.5" r="2.5" transform="rotate(-90 1444.5 1624.5)" fill="white"/>
    <circle cx="1164" cy="1627" r="5" transform="rotate(-90 1164 1627)" fill="white"/>
    <circle cx="1484" cy="1625" r="5" transform="rotate(-90 1484 1625)" fill="white"/>
    <circle cx="1204" cy="1665" r="15" transform="rotate(-90 1204 1665)" fill="white"/>
    <circle cx="1244" cy="1665" r="5" transform="rotate(-90 1244 1665)" fill="white"/>
    <circle cx="1284" cy="1665" r="5" transform="rotate(-90 1284 1665)" fill="white"/>
    <circle cx="1324" cy="1665" r="5" transform="rotate(-90 1324 1665)" fill="white"/>
    <circle cx="1044" cy="1665" r="5" transform="rotate(-90 1044 1665)" fill="white"/>
    <circle cx="1364" cy="1665" r="5" transform="rotate(-90 1364 1665)" fill="white"/>
    <circle cx="1084" cy="1665" r="15" transform="rotate(-90 1084 1665)" fill="white"/>
    <circle cx="1404.5" cy="1664.5" r="2.5" transform="rotate(-90 1404.5 1664.5)" fill="white"/>
    <circle cx="1124" cy="1665" r="15" transform="rotate(-90 1124 1665)" fill="white"/>
    <circle cx="1444.5" cy="1664.5" r="2.5" transform="rotate(-90 1444.5 1664.5)" fill="white"/>
    <circle cx="1164" cy="1665" r="15" transform="rotate(-90 1164 1665)" fill="white"/>
    <circle cx="1484" cy="1665" r="5" transform="rotate(-90 1484 1665)" fill="white"/>
    <circle cx="1204" cy="1705" r="15" transform="rotate(-90 1204 1705)" fill="white"/>
    <circle cx="1243.5" cy="1704.5" r="2.5" transform="rotate(-90 1243.5 1704.5)" fill="white"/>
    <circle cx="1283.5" cy="1704.5" r="2.5" transform="rotate(-90 1283.5 1704.5)" fill="white"/>
    <circle cx="1323.5" cy="1704.5" r="2.5" transform="rotate(-90 1323.5 1704.5)" fill="white"/>
    <circle cx="1364" cy="1705" r="5" transform="rotate(-90 1364 1705)" fill="white"/>
    <circle cx="1404.5" cy="1704.5" r="2.5" transform="rotate(-90 1404.5 1704.5)" fill="white"/>
    <circle cx="1124" cy="1705" r="15" transform="rotate(-90 1124 1705)" fill="white"/>
    <circle cx="1444.5" cy="1704.5" r="2.5" transform="rotate(-90 1444.5 1704.5)" fill="white"/>
    <circle cx="1164" cy="1705" r="15" transform="rotate(-90 1164 1705)" fill="white"/>
    <circle cx="1484" cy="1705" r="5" transform="rotate(-90 1484 1705)" fill="white"/>
    <circle cx="1204" cy="1745" r="10" transform="rotate(-90 1204 1745)" fill="white"/>
    <circle cx="1243.5" cy="1744.5" r="2.5" transform="rotate(-90 1243.5 1744.5)" fill="white"/>
    <circle cx="1283.5" cy="1744.5" r="2.5" transform="rotate(-90 1283.5 1744.5)" fill="white"/>
    <circle cx="1323.5" cy="1744.5" r="2.5" transform="rotate(-90 1323.5 1744.5)" fill="white"/>
    <circle cx="1364" cy="1745" r="5" transform="rotate(-90 1364 1745)" fill="white"/>
    <circle cx="1404" cy="1745" r="10" transform="rotate(-90 1404 1745)" fill="white"/>
    <circle cx="1124" cy="1745" r="10" transform="rotate(-90 1124 1745)" fill="white"/>
    <circle cx="1444" cy="1745" r="10" transform="rotate(-90 1444 1745)" fill="white"/>
    <circle cx="1164" cy="1745" r="10" transform="rotate(-90 1164 1745)" fill="white"/>
    <circle cx="1484" cy="1745" r="5" transform="rotate(-90 1484 1745)" fill="white"/>
    <circle cx="1204" cy="1785" r="10" transform="rotate(-90 1204 1785)" fill="white"/>
    <circle cx="1244" cy="1785" r="5" transform="rotate(-90 1244 1785)" fill="white"/>
    <circle cx="1284" cy="1785" r="5" transform="rotate(-90 1284 1785)" fill="white"/>
    <circle cx="1324" cy="1785" r="5" transform="rotate(-90 1324 1785)" fill="white"/>
    <circle cx="1364" cy="1785" r="5" transform="rotate(-90 1364 1785)" fill="white"/>
    <circle cx="1404" cy="1785" r="10" transform="rotate(-90 1404 1785)" fill="white"/>
    <circle cx="1444" cy="1785" r="10" transform="rotate(-90 1444 1785)" fill="white"/>
    <circle cx="1484" cy="1785" r="10" transform="rotate(-90 1484 1785)" fill="white"/>
    <circle cx="1284" cy="1825" r="5" transform="rotate(-90 1284 1825)" fill="white"/>
    <circle cx="1324" cy="1825" r="5" transform="rotate(-90 1324 1825)" fill="white"/>
    <circle cx="1364" cy="1825" r="5" transform="rotate(-90 1364 1825)" fill="white"/>
    <circle cx="1404" cy="1825" r="10" transform="rotate(-90 1404 1825)" fill="white"/>
    <circle cx="1444" cy="1825" r="10" transform="rotate(-90 1444 1825)" fill="white"/>
    <circle cx="1484" cy="1825" r="10" transform="rotate(-90 1484 1825)" fill="white"/>
    <circle cx="1364" cy="1865" r="5" transform="rotate(-90 1364 1865)" fill="white"/>
    <circle cx="1404" cy="1865" r="10" transform="rotate(-90 1404 1865)" fill="white"/>
    <circle cx="1444" cy="1865" r="10" transform="rotate(-90 1444 1865)" fill="white"/>
    <circle cx="1484" cy="1865" r="10" transform="rotate(-90 1484 1865)" fill="white"/>
    <circle cx="1364" cy="1905" r="5" transform="rotate(-90 1364 1905)" fill="white"/>
    <circle cx="1402" cy="1905" r="5" transform="rotate(-90 1402 1905)" fill="white"/>
    <circle cx="1442" cy="1905" r="5" transform="rotate(-90 1442 1905)" fill="white"/>
    <circle cx="1484" cy="1905" r="10" transform="rotate(-90 1484 1905)" fill="white"/>
    <circle cx="1442" cy="1945" r="5" transform="rotate(-90 1442 1945)" fill="white"/>
    <circle cx="1484" cy="1945" r="10" transform="rotate(-90 1484 1945)" fill="white"/>
    <circle cx="1441.5" cy="1987.5" r="2.5" transform="rotate(-90 1441.5 1987.5)" fill="white"/>
    <circle cx="1484" cy="1988" r="5" transform="rotate(-90 1484 1988)" fill="white"/>
    <circle cx="1441.5" cy="2027.5" r="2.5" transform="rotate(-90 1441.5 2027.5)" fill="white"/>
    <circle cx="1484" cy="2028" r="5" transform="rotate(-90 1484 2028)" fill="white"/>
    <circle cx="1484" cy="2068" r="5" transform="rotate(-90 1484 2068)" fill="white"/>
    <circle cx="1486.5" cy="1427.5" r="2.5" transform="matrix(0 -1 1 0 59 2914)" fill="white"/>
    <circle cx="1486.5" cy="1187.5" r="2.5" transform="rotate(-90 1486.5 1187.5)" fill="white"/>
    <circle cx="1486.5" cy="1227.5" r="2.5" transform="rotate(-90 1486.5 1227.5)" fill="white"/>
    <circle cx="1487" cy="1105" r="5" transform="rotate(-90 1487 1105)" fill="white"/>
    <circle cx="1486.5" cy="1147.5" r="2.5" transform="rotate(-90 1486.5 1147.5)" fill="white"/>
    <circle cx="881.5" cy="1025.5" r="2.5" transform="rotate(-90 881.5 1025.5)" fill="white"/>
    <circle cx="881.5" cy="1065.5" r="2.5" transform="rotate(-90 881.5 1065.5)" fill="white"/>
    <circle cx="881.5" cy="985.5" r="2.5" transform="rotate(-90 881.5 985.5)" fill="white"/>
    <circle cx="1204" cy="1025" r="10" transform="rotate(-90 1204 1025)" fill="white"/>
    <circle cx="1204" cy="1065" r="10" transform="rotate(-90 1204 1065)" fill="white"/>
    <circle cx="1204" cy="985" r="10" transform="rotate(-90 1204 985)" fill="white"/>
    <circle cx="924" cy="1025" r="5" transform="rotate(-90 924 1025)" fill="white"/>
    <circle cx="924" cy="1065" r="5" transform="rotate(-90 924 1065)" fill="white"/>
    <circle cx="924.5" cy="985.5" r="2.5" transform="rotate(-90 924.5 985.5)" fill="white"/>
    <circle cx="1244" cy="1025" r="10" transform="rotate(-90 1244 1025)" fill="white"/>
    <circle cx="1244" cy="1065" r="10" transform="rotate(-90 1244 1065)" fill="white"/>
    <circle cx="1244" cy="985" r="10" transform="rotate(-90 1244 985)" fill="white"/>
    <circle cx="964" cy="1025" r="5" transform="rotate(-90 964 1025)" fill="white"/>
    <circle cx="964" cy="1065" r="5" transform="rotate(-90 964 1065)" fill="white"/>
    <circle cx="964" cy="985" r="15" transform="rotate(-90 964 985)" fill="white"/>
    <circle cx="1284" cy="1027" r="5" transform="rotate(-90 1284 1027)" fill="white"/>
    <circle cx="1284" cy="1067" r="5" transform="rotate(-90 1284 1067)" fill="white"/>
    <circle cx="1284" cy="987" r="5" transform="rotate(-90 1284 987)" fill="white"/>
    <circle cx="1002" cy="1025" r="10" transform="rotate(-90 1002 1025)" fill="white"/>
    <circle cx="1002" cy="1065" r="10" transform="rotate(-90 1002 1065)" fill="white"/>
    <circle cx="1002" cy="985" r="10" transform="rotate(-90 1002 985)" fill="white"/>
    <circle cx="1324" cy="1027" r="5" transform="rotate(-90 1324 1027)" fill="white"/>
    <circle cx="1324" cy="1067" r="5" transform="rotate(-90 1324 1067)" fill="white"/>
    <circle cx="1324" cy="987" r="5" transform="rotate(-90 1324 987)" fill="white"/>
    <circle cx="1043" cy="1025" r="10" transform="rotate(-90 1043 1025)" fill="white"/>
    <circle cx="1043" cy="1065" r="10" transform="rotate(-90 1043 1065)" fill="white"/>
    <circle cx="1044" cy="986" r="15" transform="rotate(-90 1044 986)" fill="white"/>
    <circle cx="1364" cy="1027" r="5" transform="rotate(-90 1364 1027)" fill="white"/>
    <circle cx="1364" cy="1067" r="5" transform="rotate(-90 1364 1067)" fill="white"/>
    <circle cx="1364" cy="987" r="5" transform="rotate(-90 1364 987)" fill="white"/>
    <circle cx="1083" cy="1025" r="10" transform="rotate(-90 1083 1025)" fill="white"/>
    <circle cx="1083" cy="1065" r="10" transform="rotate(-90 1083 1065)" fill="white"/>
    <circle cx="1084.5" cy="985.5" r="2.5" transform="rotate(-90 1084.5 985.5)" fill="white"/>
    <circle cx="1404" cy="1027" r="5" transform="rotate(-90 1404 1027)" fill="white"/>
    <circle cx="1404" cy="1067" r="5" transform="rotate(-90 1404 1067)" fill="white"/>
    <circle cx="1404" cy="987" r="5" transform="rotate(-90 1404 987)" fill="white"/>
    <circle cx="1123.5" cy="1027.5" r="2.5" transform="matrix(0 -1 1 0 96 2151)" fill="white"/>
    <circle cx="1123.5" cy="1067.5" r="2.5" transform="matrix(0 -1 1 0 56 2191)" fill="white"/>
    <circle cx="1124.5" cy="985.5" r="2.5" transform="rotate(-90 1124.5 985.5)" fill="white"/>
    <circle cx="1447" cy="1027" r="10" transform="rotate(-90 1447 1027)" fill="white"/>
    <circle cx="1447" cy="1067" r="10" transform="rotate(-90 1447 1067)" fill="white"/>
    <circle cx="1447" cy="987" r="10" transform="rotate(-90 1447 987)" fill="white"/>
    <circle cx="1163.5" cy="1027.5" r="2.5" transform="rotate(-90 1163.5 1027.5)" fill="white"/>
    <circle cx="1163.5" cy="1067.5" r="2.5" transform="matrix(0 -1 1 0 96 2231)" fill="white"/>
    <circle cx="1164.5" cy="985.5" r="2.5" transform="rotate(-90 1164.5 985.5)" fill="white"/>
    <circle cx="1487" cy="1025" r="5" transform="rotate(-90 1487 1025)" fill="white"/>
    <circle cx="1487" cy="1065" r="5" transform="rotate(-90 1487 1065)" fill="white"/>
    <circle cx="1487" cy="985" r="5" transform="rotate(-90 1487 985)" fill="white"/>
    <circle cx="1481.5" cy="435.5" r="2.5" transform="rotate(-90 1481.5 435.5)" fill="white"/>
    <circle cx="1484" cy="465" r="10" transform="rotate(-90 1484 465)" fill="white"/>
    <circle cx="1481.5" cy="355.5" r="2.5" transform="rotate(-90 1481.5 355.5)" fill="white"/>
    <circle cx="1481.5" cy="395.5" r="2.5" transform="rotate(-90 1481.5 395.5)" fill="white"/>
    <circle cx="881.5" cy="275.5" r="2.5" transform="rotate(-90 881.5 275.5)" fill="white"/>
    <circle cx="884" cy="315" r="10" transform="rotate(-90 884 315)" fill="white"/>
    <circle cx="841.5" cy="745.5" r="2.5" transform="rotate(-90 841.5 745.5)" fill="white"/>
    <circle cx="841.5" cy="785.5" r="2.5" transform="rotate(-90 841.5 785.5)" fill="white"/>
    <circle cx="844" cy="675" r="10" transform="rotate(-90 844 675)" fill="white"/>
    <circle cx="841.5" cy="705.5" r="2.5" transform="rotate(-90 841.5 705.5)" fill="white"/>
    <circle cx="844" cy="595" r="10" transform="rotate(-90 844 595)" fill="white"/>
    <circle cx="844" cy="635" r="10" transform="rotate(-90 844 635)" fill="white"/>
    <circle cx="844" cy="515" r="10" transform="rotate(-90 844 515)" fill="white"/>
    <circle cx="844" cy="555" r="10" transform="rotate(-90 844 555)" fill="white"/>
    <circle cx="844" cy="435" r="5" transform="rotate(-90 844 435)" fill="white"/>
    <circle cx="844" cy="475" r="5" transform="rotate(-90 844 475)" fill="white"/>
    <circle cx="844" cy="355" r="5" transform="rotate(-90 844 355)" fill="white"/>
    <circle cx="844" cy="395" r="5" transform="rotate(-90 844 395)" fill="white"/>
    <circle cx="841.5" cy="275.5" r="2.5" transform="rotate(-90 841.5 275.5)" fill="white"/>
    <circle cx="844" cy="315" r="10" transform="rotate(-90 844 315)" fill="white"/>
    <circle cx="804.5" cy="595.5" r="2.5" transform="rotate(-90 804.5 595.5)" fill="white"/>
    <circle cx="804.5" cy="635.5" r="2.5" transform="rotate(-90 804.5 635.5)" fill="white"/>
    <circle cx="802" cy="515" r="10" transform="rotate(-90 802 515)" fill="white"/>
    <circle cx="804.5" cy="555.5" r="2.5" transform="rotate(-90 804.5 555.5)" fill="white"/>
    <circle cx="802" cy="435" r="5" transform="rotate(-90 802 435)" fill="white"/>
    <circle cx="802" cy="475" r="5" transform="rotate(-90 802 475)" fill="white"/>
    <circle cx="802" cy="355" r="5" transform="rotate(-90 802 355)" fill="white"/>
    <circle cx="802" cy="395" r="5" transform="rotate(-90 802 395)" fill="white"/>
    <circle cx="799.5" cy="275.5" r="2.5" transform="rotate(-90 799.5 275.5)" fill="white"/>
    <circle cx="802" cy="315" r="10" transform="rotate(-90 802 315)" fill="white"/>
    <circle cx="762" cy="515" r="10" transform="rotate(-90 762 515)" fill="white"/>
    <circle cx="764.5" cy="555.5" r="2.5" transform="rotate(-90 764.5 555.5)" fill="white"/>
    <circle cx="762" cy="435" r="10" transform="rotate(-90 762 435)" fill="white"/>
    <circle cx="762" cy="475" r="10" transform="rotate(-90 762 475)" fill="white"/>
    <circle cx="762" cy="355" r="10" transform="rotate(-90 762 355)" fill="white"/>
    <circle cx="762" cy="395" r="10" transform="rotate(-90 762 395)" fill="white"/>
    <circle cx="759.5" cy="275.5" r="2.5" transform="rotate(-90 759.5 275.5)" fill="white"/>
    <circle cx="762" cy="315" r="10" transform="rotate(-90 762 315)" fill="white"/>
    <circle cx="717" cy="395" r="5" transform="rotate(-90 717 395)" fill="white"/>
    <circle cx="717" cy="435" r="5" transform="rotate(-90 717 435)" fill="white"/>
    <circle cx="717" cy="315" r="10" transform="rotate(-90 717 315)" fill="white"/>
    <circle cx="717" cy="355" r="10" transform="rotate(-90 717 355)" fill="white"/>
    <circle cx="717" cy="275" r="5" transform="rotate(-90 717 275)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 495 360)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 495 400)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 495 440)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 495 280)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 495 320)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 459 602)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 645)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 695)" fill="white"/>
    <circle cx="15" cy="15" r="15" transform="matrix(0 -1 -1 0 469 754)" fill="white"/>
    <circle cx="15" cy="15" r="15" transform="matrix(0 -1 -1 0 469 813)" fill="white"/>
    <circle cx="15" cy="15" r="15" transform="matrix(0 -1 -1 0 469 870)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 459 522)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 459 562)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 445)" fill="white"/>
    <circle cx="5" cy="5" r="5" transform="matrix(0 -1 -1 0 459 482)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 365)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 405)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 285)" fill="white"/>
    <circle cx="10" cy="10" r="10" transform="matrix(0 -1 -1 0 464 325)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 526 358)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 526 398)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 526 278)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 526 318)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 568 318)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 568 358)" fill="white"/>
    <circle cx="2.5" cy="2.5" r="2.5" transform="matrix(0 -1 -1 0 568 278)" fill="white"/>
    <circle cx="676" cy="315" r="5" transform="rotate(-90 676 315)" fill="white"/>
    <circle cx="676" cy="355" r="5" transform="rotate(-90 676 355)" fill="white"/>
    <circle cx="676" cy="275" r="5" transform="rotate(-90 676 275)" fill="white"/>
    <circle cx="596.5" cy="315.5" r="2.5" transform="rotate(-90 596.5 315.5)" fill="white"/>
    <circle cx="596.5" cy="355.5" r="2.5" transform="rotate(-90 596.5 355.5)" fill="white"/>
    <circle cx="596.5" cy="275.5" r="2.5" transform="rotate(-90 596.5 275.5)" fill="white"/>
    <circle cx="636" cy="315" r="5" transform="rotate(-90 636 315)" fill="white"/>
    <circle cx="636" cy="275" r="5" transform="rotate(-90 636 275)" fill="white"/>
    <circle cx="1204" cy="275" r="5" transform="rotate(-90 1204 275)" fill="white"/>
    <circle cx="1204" cy="315" r="5" transform="rotate(-90 1204 315)" fill="white"/>
    <circle cx="921.5" cy="275.5" r="2.5" transform="rotate(-90 921.5 275.5)" fill="white"/>
    <circle cx="921.5" cy="315.5" r="2.5" transform="rotate(-90 921.5 315.5)" fill="white"/>
    <circle cx="1244" cy="275" r="5" transform="rotate(-90 1244 275)" fill="white"/>
    <circle cx="1244" cy="315" r="5" transform="rotate(-90 1244 315)" fill="white"/>
    <circle cx="964" cy="275" r="10" transform="rotate(-90 964 275)" fill="white"/>
    <circle cx="964" cy="315" r="10" transform="rotate(-90 964 315)" fill="white"/>
    <circle cx="1281.5" cy="275.5" r="2.5" transform="rotate(-90 1281.5 275.5)" fill="white"/>
    <circle cx="1281.5" cy="315.5" r="2.5" transform="rotate(-90 1281.5 315.5)" fill="white"/>
    <circle cx="1004" cy="275" r="10" transform="rotate(-90 1004 275)" fill="white"/>
    <circle cx="1004" cy="315" r="10" transform="rotate(-90 1004 315)" fill="white"/>
    <circle cx="1321.5" cy="275.5" r="2.5" transform="rotate(-90 1321.5 275.5)" fill="white"/>
    <circle cx="1321.5" cy="315.5" r="2.5" transform="rotate(-90 1321.5 315.5)" fill="white"/>
    <circle cx="1044" cy="275" r="10" transform="rotate(-90 1044 275)" fill="white"/>
    <circle cx="1044" cy="315" r="10" transform="rotate(-90 1044 315)" fill="white"/>
    <circle cx="1361.5" cy="275.5" r="2.5" transform="rotate(-90 1361.5 275.5)" fill="white"/>
    <circle cx="1361.5" cy="315.5" r="2.5" transform="rotate(-90 1361.5 315.5)" fill="white"/>
    <circle cx="1084" cy="275" r="5" transform="rotate(-90 1084 275)" fill="white"/>
    <circle cx="1084" cy="315" r="5" transform="rotate(-90 1084 315)" fill="white"/>
    <circle cx="1401.5" cy="275.5" r="2.5" transform="rotate(-90 1401.5 275.5)" fill="white"/>
    <circle cx="1401.5" cy="315.5" r="2.5" transform="rotate(-90 1401.5 315.5)" fill="white"/>
    <circle cx="1124" cy="275" r="5" transform="rotate(-90 1124 275)" fill="white"/>
    <circle cx="1124" cy="315" r="5" transform="rotate(-90 1124 315)" fill="white"/>
    <circle cx="1441.5" cy="275.5" r="2.5" transform="rotate(-90 1441.5 275.5)" fill="white"/>
    <circle cx="1441.5" cy="315.5" r="2.5" transform="rotate(-90 1441.5 315.5)" fill="white"/>
    <circle cx="1164" cy="275" r="5" transform="rotate(-90 1164 275)" fill="white"/>
    <circle cx="1164" cy="315" r="5" transform="rotate(-90 1164 315)" fill="white"/>
    <circle cx="1481.5" cy="275.5" r="2.5" transform="rotate(-90 1481.5 275.5)" fill="white"/>
    <circle cx="1481.5" cy="315.5" r="2.5" transform="rotate(-90 1481.5 315.5)" fill="white"/>
</g>
<g opacity="0.25" id="dots-deco-2" transform="translate(0, ${poster.shift()})">
<circle cx="123" cy="1076" r="10" transform="rotate(90 123 1076)" fill="white"/>
<circle cx="125" cy="1046" r="5" transform="rotate(90 125 1046)" fill="white"/>
<circle cx="85" cy="1076" r="10" transform="rotate(90 85 1076)" fill="white"/>
<circle cx="85" cy="1036" r="10" transform="rotate(90 85 1036)" fill="white"/>
<circle cx="43" cy="1076" r="5" transform="rotate(90 43 1076)" fill="white"/>
<circle cx="43" cy="1036" r="5" transform="rotate(90 43 1036)" fill="white"/>
<circle cx="5" cy="1086" r="5" transform="rotate(90 5 1086)" fill="white"/>
<circle cx="5" cy="1046" r="5" transform="rotate(90 5 1046)" fill="white"/>
<circle cx="123" cy="1156" r="10" transform="rotate(90 123 1156)" fill="white"/>
<circle cx="123" cy="1116" r="10" transform="rotate(90 123 1116)" fill="white"/>
<circle cx="123" cy="1236" r="10" transform="rotate(90 123 1236)" fill="white"/>
<circle cx="123" cy="1196" r="10" transform="rotate(90 123 1196)" fill="white"/>
<circle cx="123" cy="1316" r="10" transform="rotate(90 123 1316)" fill="white"/>
<circle cx="123" cy="1276" r="10" transform="rotate(90 123 1276)" fill="white"/>
<circle cx="123" cy="1396" r="10" transform="rotate(90 123 1396)" fill="white"/>
<circle cx="123" cy="1356" r="10" transform="rotate(90 123 1356)" fill="white"/>
<circle cx="85" cy="1156" r="5" transform="rotate(90 85 1156)" fill="white"/>
<circle cx="85" cy="1116" r="10" transform="rotate(90 85 1116)" fill="white"/>
<circle cx="85" cy="1236" r="5" transform="rotate(90 85 1236)" fill="white"/>
<circle cx="85" cy="1196" r="5" transform="rotate(90 85 1196)" fill="white"/>
<circle cx="85.5" cy="1315.5" r="2.5" transform="rotate(90 85.5 1315.5)" fill="white"/>
<circle cx="85" cy="1276" r="5" transform="rotate(90 85 1276)" fill="white"/>
<circle cx="85.5" cy="1395.5" r="2.5" transform="rotate(90 85.5 1395.5)" fill="white"/>
<circle cx="85.5" cy="1355.5" r="2.5" transform="rotate(90 85.5 1355.5)" fill="white"/>
<circle cx="43" cy="1156" r="5" transform="rotate(90 43 1156)" fill="white"/>
<circle cx="43" cy="1116" r="5" transform="rotate(90 43 1116)" fill="white"/>
<circle cx="43" cy="1236" r="10" transform="rotate(90 43 1236)" fill="white"/>
<circle cx="43" cy="1196" r="10" transform="rotate(90 43 1196)" fill="white"/>
<circle cx="43" cy="1316" r="10" transform="rotate(90 43 1316)" fill="white"/>
<circle cx="43" cy="1276" r="10" transform="rotate(90 43 1276)" fill="white"/>
<circle cx="43" cy="1396" r="10" transform="rotate(90 43 1396)" fill="white"/>
<circle cx="43" cy="1356" r="10" transform="rotate(90 43 1356)" fill="white"/>
<circle cx="5" cy="1166" r="5" transform="rotate(90 5 1166)" fill="white"/>
<circle cx="5" cy="1126" r="5" transform="rotate(90 5 1126)" fill="white"/>
<circle cx="5" cy="1246" r="5" transform="rotate(90 5 1246)" fill="white"/>
<circle cx="5" cy="1206" r="5" transform="rotate(90 5 1206)" fill="white"/>
<circle cx="3.5" cy="1318.5" r="2.5" transform="rotate(90 3.5 1318.5)" fill="white"/>
<circle cx="3.5" cy="1278.5" r="2.5" transform="rotate(90 3.5 1278.5)" fill="white"/>
<circle cx="3.5" cy="1398.5" r="2.5" transform="rotate(90 3.5 1398.5)" fill="white"/>
<circle cx="3.5" cy="1358.5" r="2.5" transform="rotate(90 3.5 1358.5)" fill="white"/>
<circle cx="122.5" cy="1473.5" r="2.5" transform="rotate(90 122.5 1473.5)" fill="white"/>
<circle cx="122.5" cy="1433.5" r="2.5" transform="rotate(90 122.5 1433.5)" fill="white"/>
<circle cx="163" cy="1076" r="10" transform="rotate(90 163 1076)" fill="white"/>
<circle cx="165.5" cy="1045.5" r="2.5" transform="rotate(90 165.5 1045.5)" fill="white"/>
<circle cx="163" cy="1156" r="10" transform="rotate(90 163 1156)" fill="white"/>
<circle cx="163" cy="1116" r="10" transform="rotate(90 163 1116)" fill="white"/>
<circle cx="163" cy="1236" r="10" transform="rotate(90 163 1236)" fill="white"/>
<circle cx="163" cy="1196" r="10" transform="rotate(90 163 1196)" fill="white"/>
<circle cx="163" cy="1316" r="5" transform="rotate(90 163 1316)" fill="white"/>
<circle cx="163" cy="1276" r="5" transform="rotate(90 163 1276)" fill="white"/>
<circle cx="163" cy="1396" r="5" transform="rotate(90 163 1396)" fill="white"/>
<circle cx="163" cy="1356" r="5" transform="rotate(90 163 1356)" fill="white"/>
<circle cx="162.5" cy="1473.5" r="2.5" transform="rotate(90 162.5 1473.5)" fill="white"/>
<circle cx="162.5" cy="1433.5" r="2.5" transform="rotate(90 162.5 1433.5)" fill="white"/>
<circle cx="202.5" cy="1155.5" r="2.5" transform="rotate(90 202.5 1155.5)" fill="white"/>
<circle cx="202.5" cy="1115.5" r="2.5" transform="rotate(90 202.5 1115.5)" fill="white"/>
<circle cx="205" cy="1236" r="10" transform="rotate(90 205 1236)" fill="white"/>
<circle cx="202.5" cy="1195.5" r="2.5" transform="rotate(90 202.5 1195.5)" fill="white"/>
<circle cx="205" cy="1316" r="5" transform="rotate(90 205 1316)" fill="white"/>
<circle cx="205" cy="1276" r="5" transform="rotate(90 205 1276)" fill="white"/>
<circle cx="205" cy="1396" r="5" transform="rotate(90 205 1396)" fill="white"/>
<circle cx="205" cy="1356" r="5" transform="rotate(90 205 1356)" fill="white"/>
<circle cx="204.5" cy="1473.5" r="2.5" transform="rotate(90 204.5 1473.5)" fill="white"/>
<circle cx="204.5" cy="1433.5" r="2.5" transform="rotate(90 204.5 1433.5)" fill="white"/>
<circle cx="245" cy="1236" r="10" transform="rotate(90 245 1236)" fill="white"/>
<circle cx="242.5" cy="1195.5" r="2.5" transform="rotate(90 242.5 1195.5)" fill="white"/>
<circle cx="245" cy="1316" r="10" transform="rotate(90 245 1316)" fill="white"/>
<circle cx="245" cy="1276" r="10" transform="rotate(90 245 1276)" fill="white"/>
<circle cx="245" cy="1396" r="10" transform="rotate(90 245 1396)" fill="white"/>
<circle cx="245" cy="1356" r="10" transform="rotate(90 245 1356)" fill="white"/>
<circle cx="244.5" cy="1473.5" r="2.5" transform="rotate(90 244.5 1473.5)" fill="white"/>
<circle cx="244.5" cy="1433.5" r="2.5" transform="rotate(90 244.5 1433.5)" fill="white"/>
<circle cx="290" cy="1356" r="5" transform="rotate(90 290 1356)" fill="white"/>
<circle cx="290" cy="1316" r="5" transform="rotate(90 290 1316)" fill="white"/>
<circle cx="290" cy="1436" r="10" transform="rotate(90 290 1436)" fill="white"/>
<circle cx="290" cy="1396" r="10" transform="rotate(90 290 1396)" fill="white"/>
<circle cx="290" cy="1476" r="5" transform="rotate(90 290 1476)" fill="white"/>
<circle cx="85.5" cy="1475.5" r="2.5" transform="rotate(90 85.5 1475.5)" fill="white"/>
<circle cx="85.5" cy="1435.5" r="2.5" transform="rotate(90 85.5 1435.5)" fill="white"/>
<circle cx="43" cy="1476" r="10" transform="rotate(90 43 1476)" fill="white"/>
<circle cx="43" cy="1436" r="10" transform="rotate(90 43 1436)" fill="white"/>
<circle cx="3.5" cy="1478.5" r="2.5" transform="rotate(90 3.5 1478.5)" fill="white"/>
<circle cx="3.5" cy="1438.5" r="2.5" transform="rotate(90 3.5 1438.5)" fill="white"/>
</g>
    <g id="rect-deco-1">
        <rect x="150" y="450" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="750" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="900" y="147" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="900" y="300" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="1350" y="-3" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="750" y="750" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="1200" y="750" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="450" y="1050" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect x="300" y="1200" width="150" height="150" fill="black" fill-opacity="0.03"/>
        <rect y="900" width="150" height="150" fill="black" fill-opacity="0.03"/>
    </g>

</g>
    <g id="footer-deco-1" transform="translate(0, ${poster.shift()})">
        <rect y="2086" width="1500" height="14" fill="${poster.footerColor()}"/>
        <rect x="336" y="1650" width="444" height="293" fill="white"/>
        <rect x="114" y="1888" width="835" height="50" fill="white"/>
        <rect x="219" y="1841" width="769" height="67" fill="white"/>
        <rect x="270" y="1759" width="510" height="89" fill="white"/>
        <rect x="242" y="1682" width="510" height="89" fill="white"/>
        <rect x="475" y="1515" width="643" height="285" fill="white"/>
        <rect x="445" y="1849" width="890" height="107" fill="white"/>
        <rect x="395" y="1800" width="890" height="79" fill="white"/>
        <rect x="858" y="1706" width="369" height="187" fill="white"/>
        <rect x="705" y="1423" width="369" height="187" fill="white"/>
        <rect x="421" y="1376" width="196" height="187" fill="white"/>
        <rect x="455" y="1491" width="196" height="187" fill="white"/>
        <rect x="372" y="1556" width="147" height="187" fill="white"/>
        <rect x="767" y="1348" width="196" height="187" fill="white"/>
        <rect x="854" y="1294" width="196" height="187" fill="white"/>
        <rect x="64" y="1908" width="749" height="48" fill="white"/>
    </g>

<text fill="#3A3C2E" style="white-space: pre" xml:space="preserve" font-family="Rubik" font-size="28" letter-spacing="0em"><tspan x="64" y="${2043.09 + poster.shift()}">LAZYBOT   COMMAND   HELP   PAGE   VERSION  1.2</tspan></text>
@for(var line : poster.overHalftone())
<text fill="${line.fill()}" text-anchor="${line.anchor()}" style="white-space: pre" xml:space="preserve" font-family="${line.fontFamily()}" font-size="${line.fontSize()}" letter-spacing="${line.letterSpacing()}"><tspan x="${line.x()}" y="${line.y()}">${line.text()}</tspan></text>
@endfor
<defs>
<filter id="filter0_n_2594_2342" x="0" y="0" width="1500" height="${poster.height()}" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
<feFlood flood-opacity="0" result="BackgroundImageFix"/>
<feBlend mode="normal" in="SourceGraphic" in2="BackgroundImageFix" result="shape"/>
<feTurbulence type="fractalNoise" baseFrequency="1 1" stitchTiles="stitch" numOctaves="3" result="noise" seed="7767" />
<feColorMatrix in="noise" type="luminanceToAlpha" result="alphaNoise" />
<feComponentTransfer in="alphaNoise" result="coloredNoise1">
<feFuncA type="discrete" tableValues="1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 "/>
</feComponentTransfer>
<feComposite operator="in" in2="shape" in="coloredNoise1" result="noise1Clipped" />
<feFlood flood-color="rgba(0, 0, 0, 0.04)" result="color1Flood" />
<feComposite operator="in" in2="noise1Clipped" in="color1Flood" result="color1" />
<feMerge result="effect1_noise_2594_2342">
<feMergeNode in="shape" />
<feMergeNode in="color1" />
</feMerge>
</filter>
</defs>
</svg>
