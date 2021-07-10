/*
 * This file is part of Industrial Chronicle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 ChAoS-UnItY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.chaosunity.ic.api.variant;

public enum OreVariant implements IVariant {
    TIN("tin"), SILVER("silver"), LEAD("lead"),
    ALUMINUM("aluminum"), TITANIUM("titanium"),
    IRIDIUM("iridium"), TUNGSTEN("tungsten"), OSMIUM("osmium");

    private final String name;

    OreVariant(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public int getRequiredToolLevel() {
        return switch (this) {
            case TIN -> 1;
            case SILVER, LEAD, ALUMINUM -> 2;
            case TITANIUM -> 3;
            case IRIDIUM, TUNGSTEN, OSMIUM -> 4;
        };
    }
}
