/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.votifier;

/**
 * @author yawkat
 */
public class VotifierVersion {
    private static final VotifierVersion LATEST = new VotifierVersion("1.9");

    private final String name;

    public static VotifierVersion getDefault() {
        return LATEST;
    }

    public VotifierVersion(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
