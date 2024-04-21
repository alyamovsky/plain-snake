package com.alyamovsky.snakegame;

import com.alyamovsky.snakegame.model.Snake;
import com.alyamovsky.snakegame.model.food.RegularFruit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class SnakeGame extends ApplicationAdapter {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;
    private ShapeRenderer shapeRenderer;
    private Snake snake;
    private RegularFruit food;
    private int direction;
    private boolean gameOver;
    private int score;
    private int level;
    private float elapsedTime = 0f;
    private SpriteBatch batch;
    private BitmapFont font;
    private float speed = 4; // cells per second
    private int targetFramerate;
    Runtime runtime = Runtime.getRuntime();

    Random random = new Random();

    MemoryUsageInfo memoryUsage = new MemoryUsageInfo();

    private int cycles;

    public SnakeGame(int targetFramerate) {
        this.targetFramerate = targetFramerate;
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        snake = new Snake(GRID_SIZE, GRID_SIZE / 2, GRID_SIZE / 2);
        spawnFood();
        direction = Snake.DIRECTION_RIGHT;
        gameOver = false;
        score = 0;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        level = 1;
    }

    private void spawnFood() {
        food = null;
        food = new RegularFruit(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
    }

    private void moveSnake() {
        try {
            snake.move(direction);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            gameOver = true;
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || gameOver) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && direction != Snake.DIRECTION_DOWN) {
            direction = Snake.DIRECTION_UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S) && direction != Snake.DIRECTION_UP) {
            direction = Snake.DIRECTION_DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A) && direction != Snake.DIRECTION_RIGHT) {
            direction = Snake.DIRECTION_LEFT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && direction != Snake.DIRECTION_LEFT) {
            direction = Snake.DIRECTION_RIGHT;
        }

        executeGameLogic(() -> {
            moveSnake();
            if (food.equals(snake.getHead())) {
                snake.eat(food);
                score += 1;
                spawnFood();
            }
        });

        // Clear the screen
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw a grid background
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);

        // Draw food
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(food.getX() * CELL_SIZE, food.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        shapeRenderer.setColor(Color.GREEN);
        int snakeSize = snake.getSize();
        for (int i = 0; i < snakeSize; i++) {
            Snake.Segment part = snake.getSegment(i);

            float x = part.getX() * CELL_SIZE;
            float y = part.getY() * CELL_SIZE;
            float width = CELL_SIZE;
            float height = CELL_SIZE;
            if (i == 0) {
                switch (direction) {
                    case Snake.DIRECTION_RIGHT:
                        width += CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_UP:
                        height += CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_LEFT:
                        x -= CELL_SIZE * currentCycleProgress();
                        width += CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_DOWN:
                        y -= CELL_SIZE * currentCycleProgress();
                        height += CELL_SIZE * currentCycleProgress();
                        break;
                }
            }
            if (i == snakeSize - 1) {
                switch (direction) {
                    case Snake.DIRECTION_RIGHT:
                        width -= CELL_SIZE * currentCycleProgress();
                        x += CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_UP:
                        height -= CELL_SIZE * currentCycleProgress();
                        y += CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_LEFT:
                        x -= CELL_SIZE * currentCycleProgress();
                        width -= CELL_SIZE * currentCycleProgress();
                        break;
                    case Snake.DIRECTION_DOWN:
                        y -= CELL_SIZE * currentCycleProgress();
                        height -= CELL_SIZE * currentCycleProgress();
                        break;
                }
            }

            shapeRenderer.rect(x, y, width, height);
        }

        shapeRenderer.end();

        memoryUsage = getCurrentMemoryUsage();
        batch.begin();
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Level: " + level, 10, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "Snake Size: " + snake.getSize(), 10, Gdx.graphics.getHeight() - 50);

        font.draw(batch, "Cycles: " + cycles, 150, Gdx.app.getGraphics().getHeight() - 10);

        font.draw(batch, "Used Memory: " + memoryUsage.getUsedMemoryMB() + " MB", 300, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Total Memory: " + memoryUsage.getTotalMemoryMB() + " MB", 300, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "Max Memory: " + memoryUsage.getMaxMemoryMB() + " MB", 300, Gdx.graphics.getHeight() - 50);

        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

    private void executeGameLogic(Runnable gameAction) {
        float delta = Gdx.graphics.getDeltaTime();

        if (isNextCycle(delta)) {
            elapsedTime = 0;
            cycles += 1;
            gameAction.run();
        }
    }

    private boolean isNextCycle(float delta) {
        elapsedTime += delta;
        if (elapsedTime >= 1 / speed) {
            elapsedTime = 0;
            return true;
        }

        return false;
    }

    private float currentCycleProgress() {
        return elapsedTime / (1 / speed);
    }

    private MemoryUsageInfo getCurrentMemoryUsage() {
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        // Formatting the memory usage to two decimal places
        float usedMemoryInMB = ((int) ((usedMemory / (1024f * 1024f)) * 100)) / 100f;
        float totalMemoryInMB = ((int) ((totalMemory / (1024f * 1024f)) * 100)) / 100f;
        float maxMemoryInMB = ((int) ((maxMemory / (1024f * 1024f)) * 100)) / 100f;


        // Returning the memory usage info as a new instance of MemoryUsageInfo
        return memoryUsage.update(usedMemoryInMB, totalMemoryInMB, maxMemoryInMB);
    }

    public static class MemoryUsageInfo {
        private float usedMemoryMB;
        private float totalMemoryMB;
        private float maxMemoryMB;

        public MemoryUsageInfo() {
        }

        public MemoryUsageInfo update(float usedMemoryMB, float totalMemoryMB, float maxMemoryMB) {
            this.usedMemoryMB = usedMemoryMB;
            this.totalMemoryMB = totalMemoryMB;
            this.maxMemoryMB = maxMemoryMB;

            return this;
        }

        // Getters
        public float getUsedMemoryMB() {
            return usedMemoryMB;
        }

        public float getTotalMemoryMB() {
            return totalMemoryMB;
        }

        public float getMaxMemoryMB() {
            return maxMemoryMB;
        }
    }
}