package com.fho4565.brick_lib.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WeightedBlockState extends BlockStateProvider{
    protected ArrayList<Pair<Integer,BlockState>> list;

    public WeightedBlockState() {
    }

    public WeightedBlockState(Pair<Integer,BlockState> ...pairs){
        list = new ArrayList<>(Arrays.asList(pairs));
    }
    public WeightedBlockState add(int weight,BlockState blockState){
        this.list.add(Pair.of(weight,blockState));
        return this;
    }
    /**
     * @param source
     * @return
     */
    @Override
    public BlockState sample(RandomSource source) {
        int totalWeight = 0;
        for (Pair<Integer, BlockState> pair : list) {
            int weight = pair.getLeft();
            if (weight < 0) {
                throw new IllegalArgumentException("Weight cannot be negative: " + weight);
            }
            totalWeight += weight;
        }
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be positive");
        }

        Random random = new Random();
        int target = random.nextInt(totalWeight);

        int currentSum = 0;
        for (Pair<Integer, BlockState> pair : list) {
            currentSum += pair.getLeft();
            if (target < currentSum) {
                return pair.getRight();
            }
        }

        throw new IllegalStateException("No value selected");
    }
}
