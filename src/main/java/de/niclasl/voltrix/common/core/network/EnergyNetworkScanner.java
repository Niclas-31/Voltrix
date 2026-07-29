package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix_api.energy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class EnergyNetworkScanner {

    public List<NetworkPath> scan(ServerLevel level, BlockPos startPos) {

        List<NetworkPath> paths = new ArrayList<>();

        Queue<SearchNode> open = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        open.add(new SearchNode(startPos, new ArrayList<>()));

        while (!open.isEmpty()) {

            SearchNode current = open.poll();

            if (!visited.add(current.pos)) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(current.pos);

            if (!(blockEntity instanceof IEnergyConnectable connectable)) {
                continue;
            }

            if (blockEntity instanceof IEnergyConsumer) {
                paths.add(new NetworkPath(
                        current.pos,
                        List.copyOf(current.cables)
                ));
                continue;
            }

            boolean currentIsProducer = blockEntity instanceof IEnergyProducer;
            boolean currentIsCable = blockEntity instanceof IEnergyCable;

            for (Direction direction : Direction.values()) {

                if (!connectable.getConnectionMode(direction).canOutput()) {
                    continue;
                }

                BlockPos nextPos = current.pos.relative(direction);

                BlockEntity nextEntity = level.getBlockEntity(nextPos);

                if (!(nextEntity instanceof IEnergyConnectable nextConnectable)) {
                    continue;
                }

                if (!nextConnectable.getConnectionMode(direction.getOpposite()).canInput()) {
                    continue;
                }

                if (currentIsProducer) {
                    if (!(nextEntity instanceof IEnergyCable)) {
                        continue;
                    }
                } else if (currentIsCable) {
                    if (!(nextEntity instanceof IEnergyCable)
                            && !(nextEntity instanceof IEnergyConsumer)) {
                        continue;
                    }
                } else {
                    continue;
                }

                List<BlockPos> nextCables = new ArrayList<>(current.cables);

                if (nextEntity instanceof IEnergyCable) {
                    nextCables.add(nextPos);
                }

                open.add(new SearchNode(nextPos, nextCables));
            }
        }

        return paths;
    }

    private record SearchNode(
            BlockPos pos,
            List<BlockPos> cables
    ) {}
}