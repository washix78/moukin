# moukin

jp.moukin.CSVParser2 
 
Simple CSV Parser follow the "RFC4180".

Simple , One file.

Compile/Run aimed jdk1.8 or later.

#example
test.csv:
<pre>a,b,c
"d,e","f","g"
"""h""","i","j"
"k
  l","m","n"</pre>
code:
<pre>CSVParser2 parser = new CSVParser2(new File("c:\\work\\test.csv"), "Shift_JIS");

parser.parse(record -> {
	System.out.println(record);
});</pre>
result:
<pre>[a, b, c]
[d,e, f, g]   ("d,e" is one string)
["h", i, j]
[k
  l, m, n]</pre>
