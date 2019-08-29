data class Automaton<C : Cell>(
    val cols: Int,
    val rows: Int,
    val randomCell: () -> C,
    val rule: (C, C, C) -> C
) : Iterable<String> {
    class AutomatonIterator<C : Cell>(val automaton: Automaton<C>): Iterator<String> {
        var curRow = 0
        var curCells: MutableList<C> = mutableListOf()

        override operator fun hasNext() = curRow < automaton.rows

        override operator fun next(): String {
            if (curCells.size == 0) {
                curCells = mutableListOf()
                repeat(this.automaton.cols) {
                    curCells.add(this.automaton.randomCell())
                }
            } else {
                var nextCells = mutableListOf<C>()
                repeat(automaton.cols) {
                    val left = curCells[(automaton.cols + it - 1) % automaton.cols]
                    val middle = curCells[it]
                    val right = curCells[(it + 1) % automaton.cols]
                    nextCells.add(this.automaton.rule(left, middle, right))
                }
                curCells = nextCells
            }
            var lineStrBldr = StringBuilder()
            for (cell in curCells) {
                lineStrBldr.append(cell.toString())
            }
            curRow++
            return lineStrBldr.toString()
        }
    }

    override operator fun iterator(): Iterator<String> {
        return AutomatonIterator(this)
    }

    override fun toString(): String {
        var automatonStrBldr = StringBuilder()
        for (line in this) {
            automatonStrBldr.append(line)
            automatonStrBldr.append("\n")
        }
        return automatonStrBldr.toString()
    }
}