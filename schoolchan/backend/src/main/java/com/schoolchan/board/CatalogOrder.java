package com.schoolchan.board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * How the catalog is ordered.
 *
 * <p>The board list is ordered by bump, but the catalog is an index: sometimes
 * a reader wants the newest thread, sometimes the busiest, sometimes the one
 * with the most pictures. Sorting is done over the list the board service
 * already returns, so it costs no extra query. Pinned threads stay at the top
 * in every order.
 */
public final class CatalogOrder {

    public static final List<String> SORTS = List.of("bump", "newest", "replies", "images");

    private CatalogOrder() {
    }

    /** Anything unrecognised falls back to bump order instead of erroring. */
    public static String normalize(String sort) {
        return sort != null && SORTS.contains(sort) ? sort : "bump";
    }

    public static List<ThreadEntity> sorted(List<ThreadEntity> threads, String sort) {
        List<ThreadEntity> copy = new ArrayList<>(threads);
        Comparator<ThreadEntity> order = switch (normalize(sort)) {
            case "newest" -> Comparator.comparing(ThreadEntity::getCreatedAt).reversed();
            case "replies" -> Comparator.comparingInt(ThreadEntity::getReplyCount).reversed()
                    .thenComparing(ThreadEntity::getBumpedAt, Comparator.reverseOrder());
            case "images" -> Comparator.comparingInt(ThreadEntity::getImageCount).reversed()
                    .thenComparing(ThreadEntity::getBumpedAt, Comparator.reverseOrder());
            default -> Comparator.comparing(ThreadEntity::getBumpedAt).reversed();
        };
        copy.sort(Comparator.<ThreadEntity, Boolean>comparing(t -> t.isPinned()).reversed().thenComparing(order));
        return copy;
    }
}
