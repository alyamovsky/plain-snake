package com.alyamovsky.snakegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SnakeGame extends ApplicationAdapter {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;
    private static final float INITIAL_MOVE_DELAY = 0.2f;
    private static final float GOLD_FRUIT_DURATION = 10f;
    private static final float GOLD_FRUIT_SPAWN_CHANCE = 0.5f;

    private int targetFrameRate;
    private ShapeRenderer shapeRenderer;
    private List<int[]> snake;
    private int[] food;
    private int direction;
    private boolean gameOver;
    private int score;
    private float moveDelay;
    private int level;
    private int[] goldFruit;
    private float goldFruitTimer;
    private float elapsedTime = 0f;
    private SpriteBatch batch;
    private BitmapFont font;

    public SnakeGame(int targetFrameRate) {
        this.targetFrameRate = targetFrameRate;
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        snake = new ArrayList<>();
        snake.add(new int[]{GRID_SIZE / 2, GRID_SIZE / 2});
        spawnFood();
        direction = Input.Keys.RIGHT;
        gameOver = false;
        score = 0;
        moveDelay = INITIAL_MOVE_DELAY;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        level = 1;
        goldFruit = null;
        goldFruitTimer = 0f;
        // Schedule the movement of the snake
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveSnake();
            }
        }, moveDelay, moveDelay);
    }

    private void spawnFood() {
        Random random = new Random();
        int[] newFood;
        do {
            newFood = new int[]{random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE)};
        } while (isSnakeBody(newFood) || (goldFruit != null && Arrays.equals(newFood, goldFruit)));
        food = newFood;

        if (goldFruit == null && random.nextFloat() < GOLD_FRUIT_SPAWN_CHANCE) {
            int[] newGoldFruit;
            do {
                newGoldFruit = new int[]{random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE)};
            } while (isSnakeBody(newGoldFruit) || Arrays.equals(newGoldFruit, food));
            goldFruit = newGoldFruit;
            goldFruitTimer = GOLD_FRUIT_DURATION;
        }
    }

    private boolean isSnakeBody(int[] position) {
        for (int[] part : snake) {
            if (part[0] == position[0] && part[1] == position[1]) {
                return true;
            }
        }
        return false;
    }

    private void moveSnake() {
        if (gameOver) {
            Gdx.app.exit();
        }

        int[] head = snake.get(0);
        int[] newHead = new int[2];
        System.arraycopy(head, 0, newHead, 0, head.length);

        switch (direction) {
            case Input.Keys.UP:
                newHead[1]++;
                break;
            case Input.Keys.DOWN:
                newHead[1]--;
                break;
            case Input.Keys.LEFT:
                newHead[0]--;
                break;
            case Input.Keys.RIGHT:
                newHead[0]++;
                break;
        }

        // Teleport the snake to the opposite side if it goes off the borders
        if (newHead[0] < 0) {
            newHead[0] = GRID_SIZE - 1;
        } else if (newHead[0] >= GRID_SIZE) {
            newHead[0] = 0;
        }

        if (newHead[1] < 0) {
            newHead[1] = GRID_SIZE - 1;
        } else if (newHead[1] >= GRID_SIZE) {
            newHead[1] = 0;
        }

        // Check if the snake bites itself
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i)[0] == newHead[0] && snake.get(i)[1] == newHead[1]) {
                gameOver = true;
                return;
            }
        }

        if (newHead[0] == food[0] && newHead[1] == food[1]) {
            snake.add(0, newHead);
            spawnFood();
            score += level;

            // Increase the game speed every 10 fruits
            if (score % 5 == 0) {
                level++;
                moveDelay *= 0.9f; // Decrease the move delay by 10%
                Timer.instance().clear(); // Clear the existing timer
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        moveSnake();
                    }
                }, moveDelay, moveDelay); // Schedule a new timer with the updated move delay
            }
        } else if (goldFruit != null && newHead[0] == goldFruit[0] && newHead[1] == goldFruit[1]) {
            int additionalScore = (int) (20 * Math.max(goldFruitTimer / GOLD_FRUIT_DURATION, 0.1f));
            score += additionalScore;
            goldFruit = null;
            goldFruitTimer = 0f;
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && direction != Input.Keys.DOWN) {
            direction = Input.Keys.UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S) && direction != Input.Keys.UP) {
            direction = Input.Keys.DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A) && direction != Input.Keys.RIGHT) {
            direction = Input.Keys.LEFT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && direction != Input.Keys.LEFT) {
            direction = Input.Keys.RIGHT;
        }

        // Update elapsed time
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Clear the screen
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw a grid background
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);

        // Draw snake with smooth movement
        shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            int[] part = snake.get(i);
            int[] nextPart = (i < snake.size() - 1) ? snake.get(i + 1) : part;

            float startX = part[0] * CELL_SIZE;
            float startY = part[1] * CELL_SIZE;
            float endX = nextPart[0] * CELL_SIZE;
            float endY = nextPart[1] * CELL_SIZE;

            // Handle teleportation smoothly
            if (Math.abs(endX - startX) > (float) (GRID_SIZE * CELL_SIZE) / 2) {
                if (endX < startX) {
                    endX += GRID_SIZE * CELL_SIZE;
                } else {
                    endX -= GRID_SIZE * CELL_SIZE;
                }
            }
            if (Math.abs(endY - startY) > (float) (GRID_SIZE * CELL_SIZE) / 2) {
                if (endY < startY) {
                    endY += GRID_SIZE * CELL_SIZE;
                } else {
                    endY -= GRID_SIZE * CELL_SIZE;
                }
            }

            // Calculate an interpolation factor based on elapsed time
            float interpolationFactor = Math.min(elapsedTime / moveDelay, 1f);

            // Calculate the distance to move based on the interpolation factor
            float distanceX = (endX - startX) * interpolationFactor;
            float distanceY = (endY - startY) * interpolationFactor;

            // Calculate the interpolated position
            float interpolatedX = startX + distanceX;
            float interpolatedY = startY + distanceY;

            shapeRenderer.rect(interpolatedX % (GRID_SIZE * CELL_SIZE), interpolatedY % (GRID_SIZE * CELL_SIZE), CELL_SIZE, CELL_SIZE);
        }

        // Draw food
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(food[0] * CELL_SIZE, food[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Draw gold fruit
        if (goldFruit != null) {
            shapeRenderer.setColor(Color.GOLD);
            shapeRenderer.rect(goldFruit[0] * CELL_SIZE, goldFruit[1] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        shapeRenderer.end();

        // Draw score, level, and snake size
        batch.begin();
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Level: " + level, 10, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "Snake Size: " + snake.size(), 10, Gdx.graphics.getHeight() - 50);
        batch.end();

        // Reset elapsed time if it exceeds the move delay
        if (elapsedTime >= moveDelay) {
            elapsedTime = 0f;
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}