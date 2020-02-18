
abstract class CodeTree
case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
case class Leaf(char: Char, weight: Int) extends CodeTree

type Bit = Int

def times(chars: List[Char]): List[(Char, Int)] = chars match {
  case x :: xs => {
    val (occurrences, rest) = chars.partition(c => x == c)
    (x, occurrences.length) :: times(rest)
  }
  case Nil => List()
}

def weight(tree: CodeTree): Int = tree match {
  case Fork(left, right, _, _) => weight(left) + weight(right)
  case Leaf(_, weight) => weight
}

def chars(tree: CodeTree): List[Char] = tree match {
  case Fork(left, right, _, _) => chars(left) ::: chars(right)
  case Leaf(char, _) => List(char)
}

def makeCodeTree(left: CodeTree, right: CodeTree) =
  Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))

def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = {
  freqs.map(pair=>Leaf(pair._1, pair._2)).sortBy(_.weight)
}

def combine(trees: List[CodeTree]): List[CodeTree] = trees match{
  case Nil => Nil
  case x :: Nil => x :: Nil
  case x1 :: x2 :: rx => (makeCodeTree(x1, x2) :: rx).sortBy(tree => weight(tree))
}

def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
  def decodeAcc(root: CodeTree, curTree: CodeTree, curBits: List[Bit]): List[Char] = curTree match {
    case Leaf(char, _) => {
      if(curBits.isEmpty) char :: Nil
      else char :: decodeAcc(root, root, curBits)
    }
    case Fork(left, right, _, _) => {
      curBits match {
        case 1 :: rx => decodeAcc(root, right, curBits.tail)
        case 0 :: rx => decodeAcc(root, left, curBits.tail)
        case _ => throw new Exception("illegal bit")
      }
    }
  }
  decodeAcc(tree, tree, bits)
}


/**
 * A Huffman coding tree for the French language.
 * Generated from the data given at
 *   http://fr.wikipedia.org/wiki/Fr%C3%A9quence_d%27apparition_des_lettres_en_fran%C3%A7ais
 */
val frenchCode: CodeTree = Fork(Fork(Fork(Leaf('s',121895),Fork(Leaf('d',56269),Fork(Fork(Fork(Leaf('x',5928),Leaf('j',8351),List('x','j'),14279),Leaf('f',16351),List('x','j','f'),30630),Fork(Fork(Fork(Fork(Leaf('z',2093),Fork(Leaf('k',745),Leaf('w',1747),List('k','w'),2492),List('z','k','w'),4585),Leaf('y',4725),List('z','k','w','y'),9310),Leaf('h',11298),List('z','k','w','y','h'),20608),Leaf('q',20889),List('z','k','w','y','h','q'),41497),List('x','j','f','z','k','w','y','h','q'),72127),List('d','x','j','f','z','k','w','y','h','q'),128396),List('s','d','x','j','f','z','k','w','y','h','q'),250291),Fork(Fork(Leaf('o',82762),Leaf('l',83668),List('o','l'),166430),Fork(Fork(Leaf('m',45521),Leaf('p',46335),List('m','p'),91856),Leaf('u',96785),List('m','p','u'),188641),List('o','l','m','p','u'),355071),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u'),605362),Fork(Fork(Fork(Leaf('r',100500),Fork(Leaf('c',50003),Fork(Leaf('v',24975),Fork(Leaf('g',13288),Leaf('b',13822),List('g','b'),27110),List('v','g','b'),52085),List('c','v','g','b'),102088),List('r','c','v','g','b'),202588),Fork(Leaf('n',108812),Leaf('t',111103),List('n','t'),219915),List('r','c','v','g','b','n','t'),422503),Fork(Leaf('e',225947),Fork(Leaf('i',115465),Leaf('a',117110),List('i','a'),232575),List('e','i','a'),458522),List('r','c','v','g','b','n','t','e','i','a'),881025),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u','r','c','v','g','b','n','t','e','i','a'),1486387)

/**
 * What does the secret message say? Can you decode it?
 * For the decoding use the `frenchCode' Huffman tree defined above.
 */
val secret: List[Bit] = List(0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1)

/**
 * Write a function that returns the decoded secret
 */
def decodedSecret: List[Char] = decode(frenchCode, secret)


def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
  def encodeAcc(curTree: CodeTree)(curText: List[Char]): List[Bit] = {
    curTree match{
      case Leaf(_ ,_) => {
        if(curText.length == 1) Nil
        else encodeAcc(tree)(curText.tail)
      }
      case Fork(left, right, _, _) => {
        if(chars(left).contains(curText.head)) 0 :: encodeAcc(left)(curText)
        else if(chars(right).contains(curText.head)) 1 :: encodeAcc(right)(curText)
        else throw new Exception("Character out of table")
      }
    }
  }
  encodeAcc(tree)(text)
}

encode(frenchCode)(decode(frenchCode, secret))
secret

type CodeTable = List[(Char, List[Bit])]

/**
 * This function returns the bit sequence that represents the character `char` in
 * the code table `table`.
 */
def codeBits(table: CodeTable)(char: Char): List[Bit] = {
  table.filter((pair)=>pair._1 == char).head._2
}

def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a ::: b
/**
 * Given a code tree, create a code table which contains, for every character in the
 * code tree, the sequence of bits representing that character.
 *
 * Hint: think of a recursive solution: every sub-tree of the code tree `tree` is itself
 * a valid code tree that can be represented as a code table. Using the code tables of the
 * sub-trees, think of how to build the code table for the entire tree.
 */
def convert(tree: CodeTree): CodeTable = {
  def convertAcc(curTree: CodeTree, bits: List[Bit]): CodeTable = {
    curTree match {
      case Leaf(ch, _) => List((ch, bits))
      case Fork(left, right, chs, _) => mergeCodeTables(convertAcc(left, bits ::: List(0)), convertAcc(right, bits ::: List(1)))
    }
  }
  convertAcc(tree, List())
}



convert(frenchCode)

def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
  val codeTable = convert(tree)
  def quickEncodeAcc(table: CodeTable)(text: List[Char]): List[Bit] = {
    if(text.isEmpty) Nil
    else codeBits(table)(text.head) ::: quickEncodeAcc(table)(text.tail)
  }
  quickEncodeAcc(codeTable)(text)
}

quickEncode(frenchCode)(decode(frenchCode, secret))
secret

