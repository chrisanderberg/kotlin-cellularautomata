import kotlin.random.Random
import kotlinx.coroutines.*

fun main(args: Array<String>) = runBlocking {
    data class BinaryCell(val state: Boolean) : Cell() {
        override val color: Color
            get() = if (state) Color.WHITE else Color.BLACK
    }

    data class QuadCell(val g: Boolean, val rb: Boolean) : Cell() {
        override val color: Color
            get() = if (this.g) {
                if (this.rb) {
                    Color.YELLOW
                } else {
                    Color.GREEN
                }
            } else {
                if (this.rb) {
                    Color.RED
                } else {
                    Color.BLUE
                }
            }
    }

    data class RGBCell(
        val r: Boolean,
        val g: Boolean,
        val b: Boolean
    ) : Cell() {
        override val color: Color
            get() {
                var sum = 0
                if (this.r) sum += 4
                if (this.g) sum += 2
                if (this.b) sum += 1
                return when (sum) {
                    0 -> Color.BLACK
                    4 -> Color.RED
                    2 -> Color.GREEN
                    6 -> Color.YELLOW
                    1 -> Color.BLUE
                    5 -> Color.MAGENTA
                    3 -> Color.CYAN
                    7 -> Color.WHITE
                    else -> Color.BLACK // branch should be impossible to reach
                }
            }
    }

    fun createBinaryRule(ruleNumber: Int) =
        fun(l: BinaryCell, m: BinaryCell, r: BinaryCell): BinaryCell {
            var bitMask = 1
            if (l.state) bitMask = bitMask shl 1
            if (m.state) bitMask = bitMask shl 2
            if (r.state) bitMask = bitMask shl 4
            return BinaryCell(ruleNumber and bitMask > 0)
        }

    fun createQuadRule(ruleNumber: Int): (QuadCell, QuadCell, QuadCell) -> QuadCell {
        val binaryRule = createBinaryRule(ruleNumber)
        return fun(l: QuadCell, m: QuadCell, r: QuadCell): QuadCell {
            val g0c1 = BinaryCell(l.component1())
            val g0c2 = BinaryCell(l.component2())
            val g0c3 = BinaryCell(m.component1())
            val g0c4 = BinaryCell(m.component2())
            val g0c5 = BinaryCell(r.component1())
            val g0c6 = BinaryCell(r.component2())
            val g1c2 = binaryRule(g0c1, g0c2, g0c3)
            val g1c3 = binaryRule(g0c2, g0c3, g0c4)
            val g1c4 = binaryRule(g0c3, g0c4, g0c5)
            val g1c5 = binaryRule(g0c4, g0c5, g0c6)
            val g2c3 = binaryRule(g1c2, g1c3, g1c4)
            val g2c4 = binaryRule(g1c3, g1c4, g1c5)
            return QuadCell(g2c3.state, g2c4.state)
        }
    }

    fun createRGBRule(ruleNumber: Int): (RGBCell, RGBCell, RGBCell) -> RGBCell {
        val binaryRule = createBinaryRule(ruleNumber)
        return fun(l: RGBCell, m: RGBCell, r: RGBCell): RGBCell {
            val g0c1 = BinaryCell(l.component1())
            val g0c2 = BinaryCell(l.component2())
            val g0c3 = BinaryCell(l.component3())
            val g0c4 = BinaryCell(m.component1())
            val g0c5 = BinaryCell(m.component2())
            val g0c6 = BinaryCell(m.component3())
            val g0c7 = BinaryCell(r.component1())
            val g0c8 = BinaryCell(r.component2())
            val g0c9 = BinaryCell(r.component3())
            val g1c2 = binaryRule(g0c1, g0c2, g0c3)
            val g1c3 = binaryRule(g0c2, g0c3, g0c4)
            val g1c4 = binaryRule(g0c3, g0c4, g0c5)
            val g1c5 = binaryRule(g0c4, g0c5, g0c6)
            val g1c6 = binaryRule(g0c5, g0c6, g0c7)
            val g1c7 = binaryRule(g0c6, g0c7, g0c8)
            val g1c8 = binaryRule(g0c7, g0c8, g0c9)
            val g2c3 = binaryRule(g1c2, g1c3, g1c4)
            val g2c4 = binaryRule(g1c3, g1c4, g1c5)
            val g2c5 = binaryRule(g1c4, g1c5, g1c6)
            val g2c6 = binaryRule(g1c5, g1c6, g1c7)
            val g2c7 = binaryRule(g1c6, g1c7, g1c8)
            val g3c4 = binaryRule(g2c3, g2c4, g2c5)
            val g3c5 = binaryRule(g2c4, g2c5, g2c6)
            val g3c6 = binaryRule(g2c5, g2c6, g2c7)
            return RGBCell(g3c4.state, g3c5.state, g3c6.state)
        }
    }

    val cols = 200
    val rows = 100
    fun randomAutomaton() = when ((0..3).random()) {
        1 -> Automaton<QuadCell>(
            cols = cols,
            rows = rows,
            randomCell = {QuadCell(Random.nextBoolean(), Random.nextBoolean())},
            rule = createQuadRule((0..256).random())
        )
        2 -> Automaton<RGBCell>(
            cols = cols,
            rows = rows,
            randomCell = {RGBCell(Random.nextBoolean(), Random.nextBoolean(), Random.nextBoolean())},
            rule = createRGBRule((0..256).random())
        )
        else -> Automaton<BinaryCell>(
            cols = cols,
            rows = rows,
            randomCell = {BinaryCell(Random.nextBoolean())},
            rule = createBinaryRule((0..256).random())
        )
    }

    while (true) {
        randomAutomaton().forEach {println(it)}
        delay(1000)
    }
}