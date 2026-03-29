<#macro base title>
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>${title}</title>
    <style type="text/css">
        @page { size: A4; margin: 15mm 10mm 15mm 10mm; }
        body { font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 10px; margin: 0; color: #333; }
        h1 { font-size: 14px; text-align: center; margin: 5px 0 10px; }
        h2 { font-size: 12px; margin: 8px 0 5px; }
        .header { text-align: center; margin-bottom: 10px; border-bottom: 2px solid #333; padding-bottom: 5px; }
        .header .titulo { font-size: 16px; font-weight: bold; }
        .header .subtitulo { font-size: 10px; color: #666; }
        table { width: 100%; border-collapse: collapse; margin: 5px 0; }
        th { background-color: #e8e8e8; font-weight: bold; text-align: left; padding: 4px 6px; border: 1px solid #ccc; font-size: 9px; }
        td { padding: 3px 6px; border: 1px solid #ddd; font-size: 9px; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        .text-right { text-align: right; }
        .text-center { text-align: center; }
        .total-row { background-color: #e0e0e0; font-weight: bold; }
        .footer { text-align: center; font-size: 8px; color: #999; margin-top: 10px; border-top: 1px solid #ccc; padding-top: 3px; }
    </style>
</head>
<body>
<#nested>
<div class="footer">SinapiPRO — ${emissao!""}</div>
</body>
</html>
</#macro>
