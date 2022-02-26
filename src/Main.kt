package minesweeper


import kotlin.random.Random

object Field {
    private const val M = 9
    private const val N = 9
    private var minesCount = 10
    private val minesNumbers = mutableSetOf<Int>()
    private val aroundMines = MutableList(M) { MutableList(N) { 0 } }
    private val cellsMarks = MutableList(M) { MutableList(N) { "." } }
    private val expectedCells = mutableSetOf<Int>()

    fun reset(x: Int, y: Int) {
        minesNumbers.clear()
        while (minesNumbers.size != minesCount) {
            val mine = Random.nextInt(0, M * N)
            if ((y - 1) * M + x - 1 != mine) minesNumbers.add(mine)
        }
    }

    fun calcAround() {
        for (k in minesNumbers) {
            val i = k / M
            val j = k % M
            for (ii in i - 1..i + 1)
                for (jj in j - 1..j + 1) {
                    if (ii == i && jj == j) continue
                    try {
                        if (ii * M + jj !in minesNumbers) aroundMines[ii][jj] += 1
                    } catch (e: Exception) {
                        continue
                    }
                }
        }
    }


    fun printField() {
        println(
            " │123456789│\n" +
                    "—│—————————│"
        )
        for (i in 0 until M) {
            print("${i + 1}│")
            for (j in 0 until N)
                print(cellsMarks[i][j])
            print("│\n")
        }
        println("—│—————————│")
    }

    fun setMinesCount(count: Int) {
        minesCount = count
    }

    fun setMine(x: Int, y: Int) {
        if (cellsMarks[y - 1][x - 1] != "*") cellsMarks[y - 1][x - 1] = "*"
        else cellsMarks[y - 1][x - 1] = "."
    }

    fun isNotMine(x: Int, y: Int) = (y - 1) * M + x - 1 !in minesNumbers

    fun checkMines(): Boolean {
        var countHand = 0
        var countUnexplored = 0
        for (i in 0 until M) {
            for (j in 0 until N)
                if (cellsMarks[i][j] == "*") {
                    countHand++
                    if (i * M + j !in minesNumbers) return false
                } else if (cellsMarks[i][j] == ".") countUnexplored++
        }
        if (countHand + countUnexplored != minesNumbers.size) return false
        return true
    }

    fun setFree(x: Int, y: Int) {
        val i = y - 1
        val j = x - 1

        if (i < 0 || j < 0 || i > M - 1 || j > N - 1) return
        if (aroundMines[i][j] == 0) {
            cellsMarks[i][j] = "/"
            for (ii in i - 1..i + 1)
                for (jj in j - 1..j + 1) {
                    if (i == ii && j == jj) continue
                    try {
                        if (aroundMines[ii][jj] == 0) {
                            cellsMarks[ii][jj] = "/"

                            if (ii * M + jj !in expectedCells) {
                                expectedCells.add(ii * M + jj)
                                setFree(jj + 1, ii + 1)
                            }
                        } else cellsMarks[ii][jj] = aroundMines[ii][jj].toString()
                    } catch (e: Exception) { }
                }

        } else cellsMarks[i][j] = aroundMines[i][j].toString()
    }

    fun markMines() {
        for (k in minesNumbers) {
            val i = k / M
            val j = k % M
            cellsMarks[i][j] = "X"
        }
    }
}

fun main() {
    print("How many mines do you want on the field?")
    Field.setMinesCount(readLine()!!.toInt())
    var startGame = true
    Field.printField()
    while (true) {
        print("Set/unset mines marks or claim a cell as free:")
        val (x, y, param) = readLine()!!.trim().split(" ")
        when (param) {
            "free" -> {
                if (startGame) {
                    Field.reset(x.toInt(), y.toInt())
                    Field.calcAround()
                    startGame = false
                }
                if (Field.isNotMine(x.toInt(), y.toInt())) {
                    Field.setFree(x.toInt(), y.toInt())
                } else {
                    Field.markMines()
                    Field.printField()
                    println("You stepped on a mine and failed!")
                    break
                }
            }
            "mine" -> Field.setMine(x.toInt(), y.toInt())
        }

        Field.printField()
        if (Field.checkMines()) {
            println("Congratulations! You found all the mines!")
            break
        }
    }

}
