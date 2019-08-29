abstract class Cell {
    abstract val color: Color
    override final fun toString() =
        "\u001b[${color.code}m \u001B[0m"
}