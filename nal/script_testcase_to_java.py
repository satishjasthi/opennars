testcases="""
********** variable unification
<<$x --> bird> ==> <$x --> flyer>>.
// If something is a bird, then it is a flyer.
<<$y --> bird> ==> <$y --> flyer>>. %0.00;0.70% 
// If something is a bird, then it is not a flyer. 
1
 OUT: <<$1 --> bird> ==> <$1 --> flyer>>. %0.79;0.92% 
// If something is a bird, then usually, it is a flyer. 

********** variable unification
<<$x --> bird> ==> <$x --> animal>>. 
// If something is a bird, then it is a animal. 
<<$y --> robin> ==> <$y --> bird>>. 
// If something is a robin, then it is a bird. 
3
 OUT: <<$1 --> robin> ==> <$1 --> animal>>. %1.00;0.81% 
// If something is a robin, then it is a animal. 
 OUT: <<$1 --> animal> ==> <$1 --> robin>>. %1.00;0.45% 
 // I guess that if something is a animal, then it is a robin. 

********** variable unification
<<$x --> swan> ==> <$x --> bird>>. %1.00;0.80%  
// If something is a swan, then it is a bird.
<<$y --> swan> ==> <$y --> swimmer>>. %0.80% 
// If something is a swan, then it is a swimmer.
3
 OUT: <<$1 --> swan> ==> (||,<$1 --> bird>,<$1 --> swimmer>)>. %1.00;0.72% 
// I believe that if something is a swan, then it is a bird or a swimmer.
 OUT: <<$1 --> swan> ==> (&&,<$1 --> bird>,<$1 --> swimmer>)>. %0.80;0.72% 
//I believe that if something is a swan, then usually, it is both a bird and a swimmer.
 OUT: <<$1 --> swimmer> ==> <$1 --> bird>>. %1.00;0.37% 
// I guess if something is a swimmer, then it is a bird. 
 OUT: <<$1 --> bird> ==> <$1 --> swimmer>>. %0.80;0.42% 
// I guess if something is a bird, then it is a swimmer. 
 OUT: <<$1 --> bird> <=> <$1 --> swimmer>>. %0.80;0.42% 
// I guess something is a bird, if and only if it is a swimmer. 

********** variable unification
<<bird --> $x> ==> <robin --> $x>>.
// What can be said about bird can also be said about robin.
<<swimmer --> $y> ==> <robin --> $y>>. %0.70;0.90%  
// What can be said about swimmer usually can also be said about robin.
3
 OUT: <(&&,<bird --> $1>,<swimmer --> $1>) ==> <robin --> $1>>. %1.00;0.81%
// What can be said about bird and swimmer can also be said about robin.
 OUT: <(||,<bird --> $1>,<swimmer --> $1>) ==> <robin --> $1>>. %0.70;0.81% 
// What can be said about bird or swimmer can also be said about robin.
 OUT: <<bird --> $1> ==> <swimmer --> $1>>. %1.00;0.36%
// I guess what can be said about bird can also be said about swimmer.
 OUT: <<swimmer --> $1> ==> <bird --> $1>>. %0.70;0.45%
// I guess what can be said about swimmer can also be said about bird.
 OUT: <<bird --> $1> <=> <swimmer --> $1>>. %0.70;0.45%
// I guess bird and swimmer share most properties.

********** variable unification
<(&&,<$x --> flyer>,<$x --> [chirping]>) ==> <$x --> bird>>. 
// If something can fly and chirp, then it is a bird.
<<$y --> [with-wings]> ==> <$y --> flyer>>. 
// If something has wings, then it can fly.
4
 OUT: <(&&,<$1 --> [chirping]>,<$1 --> [with-wings]>) ==> <$1 --> bird>>. %1.00;0.81% 
// If something can chirp and has wings, then it is a bird.

********** variable unification
<(&&,<$x --> flyer>,<$x --> [chirping]>, <(*, $x, worms) --> food>) ==> <$x --> bird>>. 
// If something can fly, chirp, and eats worms, then it is a bird.
<(&&,<$y --> [chirping]>,<$y --> [with-wings]>) ==> <$y --> bird>>.
// If something can chirp and has wings, then it is a bird.
10
 OUT: <(&&,<$1 --> flyer>,<(*,$1,worms) --> food>) ==> <$1 --> [with-wings]>>. %1.00;0.45%  
// If something can fly and eats worms, then I guess it has wings.
 OUT: <<$1 --> [with-wings]> ==> (&&,<$1 --> flyer>,<(*,$1,worms) --> food>)>. %1.00;0.45% 
// I guess if something has wings, then it can fly and eats worms.

********** variable unification
<(&&,<$x --> flyer>,<(*,$x,worms) --> food>) ==> <$x --> bird>>.
// If something can fly and eats worms, then it is a bird.
<<$y --> flyer> ==> <$y --> [with-wings]>>.
// If something can fly, then it has wings.
12
 OUT: <(&&,<$1 --> [with-wings]>,<worms --> (/,food,$1,_)>) ==> <$1 --> bird>>. %1.00;0.45% 
// If something has wings and eats worms, then I guess it is a bird.

****** variable elimination
<<$x --> bird> ==> <$x --> animal>>.
// If something is a bird, then it is an animal.
<robin --> bird>. 
// A robin is a bird.
3
 OUT: <robin --> animal>. %1.00;0.81%  
// A robin is an animal.

********** variable elimination
<<$x --> bird> ==> <$x --> animal>>. 
// If something is a bird, then it is an animal.
<tiger --> animal>. 
// A tiger is an animal.
3
 OUT: <tiger --> bird>. %1.00;0.45%  
// I guess that a tiger is a bird.

********** variable elimination
<<$x --> animal> <=> <$x --> bird>>. 
// Something is a animal if and only if it is a bird.
<robin --> bird>.
// A robin is a bird.
3
 OUT: <robin --> animal>. %1.00;0.81%  
// A robin is a animal.

********** variable elimination
(&&,<#x --> bird>,<#x --> swimmer>). 
// Some bird can swim.
<swan --> bird>. %0.90%
// Swan is a type of bird.
3
 OUT: <swan --> swimmer>. %0.90;0.43%  
// I guess swan can swim.

********** variable elimination
<{Tweety} --> [with-wings]>.
// Tweety has wings.
<(&&,<$x --> [chirping]>,<$x --> [with-wings]>) ==> <$x --> bird>>.
// If something can chirp and has wings, then it is a bird.
6
 OUT: <<{Tweety} --> [chirping]> ==> <{Tweety} --> bird>>. %1.00;0.81%
// If Tweety can chirp, then it is a bird.

********** variable elimination
<(&&,<$x --> flyer>,<$x --> [chirping]>, <(*, $x, worms) --> food>) ==> <$x --> bird>>. 
// If something can fly, chirp, and eats worms, then it is a bird.
<{Tweety} --> flyer>.
// Tweety can fly.
3
 OUT: <(&&,<{Tweety} --> [chirping]>,<(*,{Tweety},worms) --> food>) ==> <{Tweety} --> bird>>. %1.00;0.81%
// If Tweety can chirp and eats worms, then it is a bird.

********** multiple variable elimination
<(&&,<$x --> key>,<$y --> lock>) ==> <$y --> (/,open,$x,_)>>.  
// Every lock can be opened by every key.
<{lock1} --> lock>. 
// Lock-1 is a lock.
5
 OUT: <<$1 --> key> ==> <{lock1} --> (/,open,$1,_)>>. %1.00;0.81%
// Lock-1 can be opened by every key.

********** multiple variable elimination
<<$x --> lock> ==> (&&,<#y --> key>,<$x --> (/,open,#y,_)>)>.  
// Every lock can be opened by some key.
<{lock1} --> lock>. 
// Lock-1 is a lock.
3
 OUT: (&&,<#1 --> key>,<{lock1} --> (/,open,#1,_)>). %1.00;0.81% 
// Some key can open Lock-1.

********** multiple variable elimination
(&&,<#x --> lock>,<<$y --> key> ==> <#x --> (/,open,$y,_)>>).  
// There is a lock that can be opened by every key.
<{lock1} --> lock>.
// Lock-1 is a lock.
3
 OUT: <<$1 --> key> ==> <{lock1} --> (/,open,$1,_)>>. %1.00;0.43% 
// I guess Lock-1 can be opened by every key.

********** multiple variable elimination
(&&,<#x --> (/,open,#y,_)>,<#x --> lock>,<#y --> key>).  
// There is a key that can open some lock.
<{lock1} --> lock>.
// Lock-1 is a lock.
4
 OUT: (&&,<#1 --> key>,<(*,#1,{lock1}) --> open>). %1.00;0.43%
// I guess there is a key that can open Lock-1.

********** variable introduction
<swan --> bird>.  
// A swan is a bird.
<swan --> swimmer>. %0.80% 
// A swan is usually a swimmer.
3
 OUT: <<$1 --> bird> ==> <$1 --> swimmer>>. %0.80;0.45% 
// I guess a bird is usually a swimmer.
 OUT: <<$1 --> swimmer> ==> <$1 --> bird>>. %1.00;0.39% 
// I guess a swimmer is a bird.
 OUT: <<$1 --> bird> <=> <$1 --> swimmer>>. %0.80;0.45% 
// I guess a bird is usually a swimmer, and the other way around.
 OUT: (&&,<#1 --> bird>,<#1 --> swimmer>). %0.80;0.81% 
// Some bird can swim.

********** variable introduction
<gull --> swimmer>. 
//A gull is a swimmer.
<swan --> swimmer>. %0.80% 
//Usually, a swan is a swimmer.
3
 OUT: <<gull --> $1> ==> <swan --> $1>>. %0.80;0.45% 
// I guess what can be said about gull usually can also be said about swan.
 OUT: <<swan --> $1> ==> <gull --> $1>>. %1.00;0.39%  
// I guess what can be said about swan can also be said about gull.
 OUT: <<gull --> $1> <=> <swan --> $1>>. %0.80;0.45%
// I guess gull and swan share most properties.
 OUT: (&&,<gull --> #1>,<swan --> #1>). %0.80;0.81%
// Gull and swan have some common property.

********** variables introduction
<{key1} --> (/,open,_,{lock1})>. 
// Key-1 opens Lock-1.
<{key1} --> key>.
// Key-1 is a key.
6
 OUT: <<$1 --> key> ==> <$1 --> (/,open,_,{lock1})>>. %1.00;0.45%
// I guess every key can open Lock-1.
 OUT: (&&,<#1 --> (/,open,_,{lock1})>,<#1 --> key>). %1.00;0.81%
// Some key can open Lock-1.

********** multiple variables introduction
<<$x --> key> ==> <{lock1} --> (/,open,$x,_)>>. 
// Lock-1 can be opened by every key.
<{lock1} --> lock>. 
// Lock-1 is a lock.
15
 OUT: (&&,<#1 --> lock>,<<$2 --> key> ==> <#1 --> (/,open,$2,_)>>). %1.00;0.81%
// There is a lock that can be opened by every key.
 OUT: <(&&,<$1 --> key>,<$2 --> lock>) ==> <$2 --> (/,open,$1,_)>>. %1.00;0.45%
// I guess every lock can be opened by every key.

********** multiple variables introduction
(&&,<#x --> key>,<{lock1} --> (/,open,#x,_)>).  
// Lock-1 can be opened by some key.
<{lock1} --> lock>. 
// Lock-1 is a lock.
15
 OUT: (&&,<#1 --> key>,<#2 --> lock>,<#2 --> (/,open,#1,_)>). %1.00;0.81%
// There is a key that can open some lock.
 OUT: <<$1 --> lock> ==> (&&,<#2 --> key>,<$1 --> (/,open,#2,_)>)>. %1.00;0.45% 
// I guess every lock can be opened by some key.

***** second level variable unification
  IN: (&&,<#1 --> lock>,<<$2 --> key> ==> <#1 --> (/,open,$2,_)>>). %1.00;0.90% 
//there is a lock which is opened by all keys
  IN: <{key1} --> key>. %1.00;0.90% 
//key1 is a key
5
 OUT: (&&,<#1 --> lock>,<#1 --> (/,open,{key1},_)>). %1.00;0.81%
//there is a lock which is opened by key1

***** second level variable unification
  IN: <<$1 --> lock> ==> (&&,<#2 --> key>,<$1 --> (/,open,#2,_)>)>. %1.00;0.90% 
//all locks are opened by some key
  IN: <{key1} --> key>. %1.00;0.90% 
//key1 is a key
5
 OUT: <<$1 --> lock> ==> <$1 --> (/,open,{key1},_)>>. %1.00;0.43%  
//maybe all locks are opened by key1

***** second variable introduction (induction)
  IN: <<lock1 --> (/,open,$1,_)> ==> <$1 --> key>>.
//if something opens lock1, it is a key
  IN: <lock1 --> lock>.
//lock1 is a key
7
 OUT: <(&&,<#1 --> lock>,<#1 --> (/,open,$2,_)>) ==> <$2 --> key>>. %1.00;0.45%
//there is a lock with the property that when opened by something, this something is a key (induction)

***** variable elimination (deduction)
  IN: <lock1 --> lock>. %1.00;0.90%
//lock1 is a lock
  IN: <(&&,<#1 --> lock>,<#1 --> (/,open,$2,_)>) ==> <$2 --> key>>. %1.00;0.90% 
//there is a lock with the property that when opened by something, this something is a key
4
 OUT: <<lock1 --> (/,open,$1,_)> ==> <$1 --> key>>. %1.00;0.43%
//whatever opens lock1 is a key

***** abduction with variable elimination (abduction)
  IN: <<lock1 --> (/,open,$1,_)> ==> <$1 --> key>>. %1.00;0.90% {0 : 1} 
//whatever opens lock1 is a key
  IN: <(&&,<#1 --> lock>,<#1 --> (/,open,$2,_)>) ==> <$2 --> key>>. %1.00;0.90% {0 : 2} 
//there is a lock with the property that when opened by something, this something is a key
10
 OUT: <lock1 --> lock>. %1.00;0.45% {10 : 1;2} 
//lock1 is a lock

"""



def lineToJava(l,a):
    l=l.replace(";",",").replace("%","").replace(">. ",">.").replace(">! ",">!").replace(">? ",">?").replace("). ",").").replace(")! ",")!").replace(")? ",")?")
    func = "n.believe(\""
    if "OUT:" in l:
        func = "n.mustBelieve(100,\""
    l=l.replace("OUT: ","").replace("OUT:","")
    if ">?" in l or ")?" in l:
        func = "n.ask(\""
    if ">!" in l or ")!" in l:
        func = "n.goal(\""
    return (func+l+"\").en(\""+a.replace("// ","").replace("//","")+"\");").replace(". \"",".\"").replace("? \"","?\"").replace("! \"","!\"").replace("  IN: ","")

def transform(test):
    lines = [z for z in test.split("\n") if any(c.isalpha() for c in z) or "//" in z]
    title = lines[0].replace(" ","_")[1:]
    body=""
    
    for i in xrange(2,len(lines),2):
        body+=lineToJava(lines[i-1],lines[i])+"\n"
    
    header="""
@Test
public void """ + title + """() throws InvalidInputException {
""" + body + "}"
    return header
    

tests=[z for z in testcases.split("*****") if "//" in z]
print transform(tests[0])


text = ""
for s in tests:
    text += transform(s)+"\n\n"

text_file = open("Output_.txt", "w")
text_file.write(text)
text_file.close()
