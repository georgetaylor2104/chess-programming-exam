package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = pieceColor;
        ChessPiece piece = board.getPiece(myPosition);
        PieceMovesCalculator calculator;
        if (piece.getPieceType() == PieceType.KING) {
            calculator = new KingMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            calculator = new QueenMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else if (piece.getPieceType() == PieceType.PAWN) {
            calculator = new PawnMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            calculator = new RookMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            calculator = new KnightMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else if (piece.getPieceType() == PieceType.BISHOP) {
            calculator = new BishopMoves();
            return calculator.pieceMoves(board, myPosition, color);
        }
        else {
            return List.of();
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

record Pair(int first, int second) {}

interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color);

    default boolean isClearOrTakeable(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        return (board.isInBounds(position) && (board.getPiece(position) == null || board.getPiece(position).getTeamColor() != color));
    }

    default boolean enemyCollision(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        return board.isInBounds(position) && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != color;
    }
}

class KingMoves implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        List<Pair> pairList = List.of(new Pair(1,0), new Pair(1,1), new Pair(1,-1), new Pair(0,1), new Pair(0,-1), new Pair(-1,1), new Pair(-1,0), new Pair(-1,-1));

        for (Pair pair :pairList) {
            ChessPosition positionToTry = new ChessPosition(myPosition.getRow()+pair.first(), myPosition.getColumn()+pair.second());

            if (isClearOrTakeable(board, positionToTry, color)) {
                moveList.add((new ChessMove(myPosition, positionToTry, null)));
            }
        }

        return moveList;
    }
}

class QueenMoves implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        List<Pair> pairList = List.of(new Pair(1,0), new Pair(0,1), new Pair(0,-1), new Pair(-1,0), new Pair(1,1), new Pair(1,-1), new Pair(-1,-1), new Pair(-1,1));

        for (Pair pair :pairList) {
            ChessPosition currentPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            ChessPosition positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());

            while (isClearOrTakeable(board, positionToTry, color)) {
                moveList.add((new ChessMove(myPosition, positionToTry, null)));
                if (enemyCollision(board, positionToTry, color)) {
                    break;
                }
                currentPosition = positionToTry;
                positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());
            }
        }

        return moveList;
    }
}

class PawnMoves implements PieceMovesCalculator {
    //string enemy checker

    //list pair returner that interprets string

    //first move block

    //check for promotion

    //different moves for black vs white

    public String enemiesPresent(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        String enemies = "";
        if (color == ChessGame.TeamColor.WHITE) {
            if (enemyCollision(board, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), color)) {
                enemies += "R";
            }
            if (enemyCollision(board, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), color)) {
                enemies += "L";
            }
            if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) != null) {
                enemies += "F";
            }
        }
        else {
            if (enemyCollision(board, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), color)) {
                enemies += "R";
            }
            if (enemyCollision(board, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), color)) {
                enemies += "L";
            }
            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) != null) {
                enemies += "F";
            }
        }

        return enemies;
    }

    public List<Pair> movePairs(String enemies, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return switch (enemies) {
                case "" -> List.of(new Pair(1, 0));
                case "R" -> List.of(new Pair(1, 0), new Pair(1, 1));
                case "L" -> List.of(new Pair(1, 0), new Pair(1, -1));
                case "RL" -> List.of(new Pair(1, 0), new Pair(1, 1), new Pair(1, -1));
                case "RLF" -> List.of(new Pair(1, 1), new Pair(1, -1));
                case "RF" -> List.of(new Pair(1, 1));
                case "LF" -> List.of(new Pair(1, -1));
                default -> List.of();
            };
        } else {
            return switch (enemies) {
                case "" -> List.of(new Pair(-1, 0));
                case "R" -> List.of(new Pair(-1, 0), new Pair(-1, 1));
                case "L" -> List.of(new Pair(-1, 0), new Pair(-1, -1));
                case "RL" -> List.of(new Pair(-1, 0), new Pair(-1, 1), new Pair(-1, -1));
                case "RLF" -> List.of(new Pair(-1, 1), new Pair(-1, -1));
                case "RF" -> List.of(new Pair(-1, 1));
                case "LF" -> List.of(new Pair(-1, -1));
                default -> List.of();
            };
        }
    }

    public boolean atStart(ChessPosition myPosition, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return myPosition.getRow() == 2;
        }
        else {
            return myPosition.getRow() == 7;
        }
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        String enemies = enemiesPresent(board, myPosition, color);
        List<Pair> pairList = movePairs(enemies, color);
        Pair firstMoveWhite = new Pair(2,0);
        Pair firstMoveBlack = new Pair(-2,0);

        if (atStart(myPosition, color)) {
            ChessPosition posToTry;
            ChessPosition priorMove;
            if (color == ChessGame.TeamColor.WHITE) {
                posToTry = new ChessPosition(myPosition.getRow() + firstMoveWhite.first(), myPosition.getColumn() + firstMoveWhite.second());
                priorMove = new ChessPosition(myPosition.getRow() + firstMoveWhite.first() - 1, myPosition.getColumn() + firstMoveWhite.second());
            }
            else {
                posToTry = new ChessPosition(myPosition.getRow() + firstMoveBlack.first(), myPosition.getColumn() + firstMoveBlack.second());
                priorMove = new ChessPosition(myPosition.getRow() + firstMoveBlack.first() + 1, myPosition.getColumn() + firstMoveBlack.second());
            }
            if (board.isInBounds(priorMove) && board.getPiece(priorMove) == null) {
                if (board.isInBounds(posToTry) && board.getPiece(posToTry) == null) {
                    moveList.add(new ChessMove(myPosition, posToTry, null));
                }
            }
        }

        switch (color) {
            case WHITE:
                for (Pair pair : pairList) {
                    ChessPosition positionToTry = new ChessPosition(myPosition.getRow() + pair.first(), myPosition.getColumn() + pair.second());
                    if (positionToTry.getRow() == 8) {
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.QUEEN));
                    }
                    else {
                        moveList.add(new ChessMove(myPosition, positionToTry, null));
                    }
                }
                break;
            case BLACK:
                for (Pair pair : pairList) {
                    ChessPosition positionToTry = new ChessPosition(myPosition.getRow() + pair.first(), myPosition.getColumn() + pair.second());
                    if (positionToTry.getRow() == 1) {
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, positionToTry, ChessPiece.PieceType.QUEEN));
                    }
                    else {
                        moveList.add(new ChessMove(myPosition, positionToTry, null));
                    }
                }
                break;
        }

        return moveList;
    }
}

class KnightMoves implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        List<Pair> pairList = List.of(new Pair(2,1), new Pair(2,-1), new Pair(1,2), new Pair(-1,2), new Pair(-2,-1), new Pair(-2,1), new Pair(1,-2), new Pair(-1,-2));

        for (Pair pair :pairList) {
            ChessPosition positionToTry = new ChessPosition(myPosition.getRow()+pair.first(), myPosition.getColumn()+pair.second());

            if (isClearOrTakeable(board, positionToTry, color)) {
                moveList.add((new ChessMove(myPosition, positionToTry, null)));
            }
        }

        return moveList;
    }
}

class RookMoves implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        List<Pair> pairList = List.of(new Pair(1,0), new Pair(0,1), new Pair(0,-1), new Pair(-1,0));

        for (Pair pair :pairList) {
            ChessPosition currentPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            ChessPosition positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());

            while (isClearOrTakeable(board, positionToTry, color)) {
                moveList.add((new ChessMove(myPosition, positionToTry, null)));
                if (enemyCollision(board, positionToTry, color)) {
                    break;
                }
                currentPosition = positionToTry;
                positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());
            }
        }

        return moveList;
    }
}

class BishopMoves implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        List<ChessMove> moveList = new ArrayList<>();
        List<Pair> pairList = List.of(new Pair(1,1), new Pair(1,-1), new Pair(-1,-1), new Pair(-1,1));

        for (Pair pair :pairList) {
            ChessPosition currentPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            ChessPosition positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());

            while (isClearOrTakeable(board, positionToTry, color)) {
                moveList.add((new ChessMove(myPosition, positionToTry, null)));
                if (enemyCollision(board, positionToTry, color)) {
                    break;
                }
                currentPosition = positionToTry;
                positionToTry = new ChessPosition(currentPosition.getRow()+pair.first(), currentPosition.getColumn()+pair.second());
            }
        }

        return moveList;
    }
}
