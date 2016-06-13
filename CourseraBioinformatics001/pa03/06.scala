"Randomized Motif Search"

val input = scala.io.Source.fromFile("06.input").getLines()
val kt = input.next().split(" ").map(_.toInt)
val (k, t) = (kt(0), kt(1))
val dnas = input.toList

val table = Map('A'-> 0, 'C' -> 1, 'G' -> 2, 'T' -> 3)
val revt = "ACGT"

val rand = new scala.util.Random()
def randomInit = dnas.map(s => { val start = rand.nextInt(s.size - k + 1); s.slice(start, start + k)})

def randomMs = refineWhile(randomInit) { ms =>
  val nms = dnas.map(patt(_, mat(ms)))
  val b = score(nms) < score(ms)
  (nms, b)
}

var i = 0
lazy val ms = refineWhile2(randomInit) { (now, ms) =>
  val nms = randomMs
  val rep = score(nms) < score(now)
  i += 1
  (nms, rep, i <= 1000)
}

println(ms.mkString("\n"))

def refineWhile[T](init: T)(body: T => (T, Boolean)) = {
  var now = init
  var conti = true
  while(conti) {
    val r = body(now)
    conti = r._2
    if (conti) now = r._1
  }
  now
}
def refineWhile2[T](init: T)(body: (T, T) => (T, Boolean, Boolean)) = {
  var now = init
  var last = init
  var conti = true
  while(conti) {
    val r = body(now, last)
    last = r._1
    val rep = r._2
    if (rep) now = last
    conti = r._3
  }
  now
}

def mat(ms: Seq[String]) = (0 until ms(0).length).map(n => {
  val list = ms.map(_(n))
  revt.map(c => (1 + list.count(_ == c)).toFloat / (4 + list.size))
})

def patt(text: String, mat: Seq[Seq[Float]]) = text.sliding(k).maxBy(s => s.zipWithIndex.foldLeft(1f)((p, c) => p * mat(c._2)(table(c._1))))

def consensuc(ms: List[String]) = (0 until ms(0).length).map(n => ms.map(_(n)).groupBy(c => c).toList.sortBy(_._2.size).last._1)

def dis(a: Seq[Char], b: Seq[Char]) = a.zip(b).count(pr => pr._1 != pr._2)

def score(ms: List[String]): Float = {val c = consensuc(ms); ms.map(dis(_, c)).sum}
