package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.*;
import de.niclasl.voltrix_api.energy.flow.CableFlow;
import de.niclasl.voltrix_api.network.IEnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class EnergyNetworkImpl implements IEnergyNetwork {

    private final EnergyTransferEngine transferEngine = new EnergyTransferEngine();

    private final Set<BlockPos> nodes = new HashSet<>();

    private final Set<BlockPos> producers = new HashSet<>();

    private final Set<BlockPos> consumers = new HashSet<>();

    private final Map<BlockPos, CableFlow> cables = new HashMap<>();

    private final List<NetworkPath> paths = new ArrayList<>();

    private boolean dirty = true;

    public void markDirty() {
        dirty = true;
    }

    @Override
    public void tick(ServerLevel level) {

        if (dirty) {
            rebuild(level);
            scan(level);
            buildPaths(level);
            dirty = false;
        }

        for (NetworkPath path : paths) {
            transferEngine.transfer(level, path);
        }
    }

    @Override
    public void addNode(BlockPos pos) {
        if (nodes.add(pos)) {
            markDirty();
        }
    }

    @Override
    public void removeNode(BlockPos pos) {
        if (nodes.remove(pos)) {
            markDirty();
        }
    }

    @Override
    public Collection<BlockPos> getNodes() {
        return Set.copyOf(nodes);
    }

    public void rebuild(ServerLevel level) {

        if (nodes.isEmpty()) {
            consumers.clear();
            producers.clear();
            cables.clear();
            paths.clear();
            return;
        }

        BlockPos start = nodes.iterator().next();

        rebuild(level, start);
    }

    public void rebuild(ServerLevel level, BlockPos start) {
        Set<BlockPos> reachable = new HashSet<>();

        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            if (!reachable.add(pos)) {
                continue;
            }

            BlockEntity entity = level.getBlockEntity(pos);

            if (!(entity instanceof IEnergyConnectable connectable)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                ConnectionMode mode = connectable.getConnectionMode(direction);

                if (!mode.canInput() && !mode.canOutput()) {
                    continue;
                }

                BlockPos next = pos.relative(direction);

                BlockEntity nextEntity = level.getBlockEntity(next);

                if (!(nextEntity instanceof IEnergyConnectable nextConnectable)) {
                    continue;
                }

                ConnectionMode nextMode = nextConnectable.getConnectionMode(direction.getOpposite());

                if (!nextMode.canInput() && !nextMode.canOutput()) {
                    continue;
                }

                queue.add(next);
            }
        }

        nodes.clear();
        nodes.addAll(reachable);
    }

    private void scan(ServerLevel level) {
        consumers.clear();
        producers.clear();
        cables.clear();

        for (BlockPos pos : nodes) {

            BlockEntity entity = level.getBlockEntity(pos);

            if (entity instanceof IEnergyProducer producer && !(producer instanceof IEnergyTransmission)) {
                producers.add(pos);
            }

            if (entity instanceof IEnergyConsumer) {
                consumers.add(pos);
            }

            if (entity instanceof AbstractCableEntity) {
                cables.put(
                        pos,
                        new CableFlow(
                                pos,
                                EnumSet.noneOf(Direction.class)
                        )
                );
            }
        }
    }

    private void buildPaths(ServerLevel level) {
        paths.clear();

        for (BlockPos consumer : consumers) {

            NetworkPath path = buildPath(level, consumer);

            if (path != null) {
                paths.add(path);
            }
        }
    }

    private NetworkPath buildPath(ServerLevel level, BlockPos consumer) {

        Set<BlockPos> foundProducers = new LinkedHashSet<>();
        Set<CableFlow> foundCables = new LinkedHashSet<>();
        Set<BlockPos> foundTransmissions = new LinkedHashSet<>();
        Set<BlockPos> foundReceivers = new LinkedHashSet<>();

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(consumer);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            if (!visited.add(pos)) {
                continue;
            }

            BlockEntity entity = level.getBlockEntity(pos);

            if (!(entity instanceof IEnergyConnectable connectable)) {
                continue;
            }

            if (entity instanceof IEnergyProducer && !(entity instanceof IEnergyTransmission)) {
                foundProducers.add(pos);
            }

            if (entity instanceof IPowerStateReceiver) {
                foundReceivers.add(pos);
            }

            if (entity instanceof AbstractCableEntity) {
                CableFlow flow = cables.get(pos);

                if (flow != null) {
                    foundCables.add(flow);
                }
            }

            if (entity instanceof IEnergyTransmission && !(entity instanceof AbstractCableEntity)) {
                foundTransmissions.add(pos);
            }

            for (Direction direction : Direction.values()) {

                ConnectionMode mode = connectable.getConnectionMode(direction);

                if (!mode.canInput()) {
                    continue;
                }

                BlockPos next = pos.relative(direction);

                if (!nodes.contains(next)) {
                    continue;
                }

                BlockEntity nextEntity = level.getBlockEntity(next);

                if (!(nextEntity instanceof IEnergyConnectable nextConnectable)) {
                    continue;
                }


                ConnectionMode nextMode = nextConnectable.getConnectionMode(direction.getOpposite());

                if (!nextMode.canOutput()) {
                    continue;
                }

                queue.add(next);
            }
        }

        if (foundProducers.isEmpty()) {
            return null;
        }

        return new NetworkPath(
                consumer,
                List.copyOf(foundProducers),
                List.copyOf(foundCables),
                List.copyOf(foundTransmissions),
                List.copyOf(foundReceivers)
        );
    }
}