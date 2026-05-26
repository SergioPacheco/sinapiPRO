@import java.util.Map
@import java.util.List

@param String title
@param String company = "SinapiPRO"
@param List<String> headers
@param List<Map<String, Object>> rows

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><style>
@page { size: A4; margin: 20mm 15mm 25mm 15mm; }
body { font-family: Arial, sans-serif; font-size: 11px; color: #333; }
h1 { color: #1a56db; font-size: 16px; border-bottom: 2px solid #1a56db; padding-bottom: 6px; }
table { width: 100%; border-collapse: collapse; margin-top: 12px; }
th { background: #1a56db; color: white; padding: 6px 8px; text-align: left; font-size: 10px; }
td { border-bottom: 1px solid #e5e7eb; padding: 5px 8px; font-size: 10px; }
tr:nth-child(even) td { background: #f9fafb; }
.meta { font-size: 9px; color: #666; margin-bottom: 10px; }
</style></head>
<body>
<h1>${title}</h1>
<div class="meta">${company} | Gerado em: ${java.time.LocalDate.now()}</div>
<table>
<tr>
@for(String h : headers)
    <th>${h}</th>
@endfor
</tr>
@for(Map<String, Object> row : rows)
<tr>
@for(String h : headers)
    <td>${row.getOrDefault(h, "")}</td>
@endfor
</tr>
@endfor
</table>
</body>
</html>
