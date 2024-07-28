/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.util;

public class TimeUtil {
    private static final int DAY_INDEX           = 4;
    private static final int HOUR_INDEX          = 3;
    private static final int MILLI_SECONDS_INDEX = 0;
    private static final int MINUTE_INDEX        = 2;
    private static final int SECONDS_INDEX       = 1;
    private static final int WEEK_INDEX          = 5;

    public static String create24hDurationString(long aTime, final boolean aUseSeconds, final boolean useMilliSeconds, final boolean aUseCharacters, final boolean aPrintLeadingZeros, boolean fixedSize) {
        String prefix;
        if (aTime < 0) {
            prefix = "-";
            aTime  = -aTime;
        } else {
            prefix = "";
        }
        String               _result     = "";
        final long[]         _timePieces = {0, 0, 0, 0, 0, 0};
        final RcTimeStruct[] _time       = {new RcTimeStruct("", "ms", 3), new RcTimeStruct(":", "s", 2), new RcTimeStruct(" ", "m", 2), new RcTimeStruct(":", "h", 2), new RcTimeStruct(" ", "d", 1), new RcTimeStruct(" ", "w", 2)};
        _timePieces[WEEK_INDEX]          = aTime / (86400000L * 7L);//assuming a 5 days working week
                                           aTime -= _timePieces[WEEK_INDEX] * (86400000L * 7L);//assuming a 5 days working week
        _timePieces[DAY_INDEX]           = aTime / 86400000L;//assuming 7.5h day
                                           aTime -= _timePieces[DAY_INDEX] * 86400000L;//assuming 7.5h day
        _timePieces[HOUR_INDEX]          = aTime / 3600000L;
                                           aTime -= _timePieces[HOUR_INDEX] * 3600000L;
        _timePieces[MINUTE_INDEX]        = aTime / 60000L;
                                           aTime -= _timePieces[MINUTE_INDEX] * 60000L;
        _timePieces[SECONDS_INDEX]       = aTime / 1000L;
                                           aTime -= _timePieces[SECONDS_INDEX] * 1000L;
        _timePieces[MILLI_SECONDS_INDEX] = aTime;

        boolean _weFoundTheFirstNonezeroValue = aPrintLeadingZeros;
        int     _indexEnd                     = 0;
        if (!useMilliSeconds) {
            _indexEnd = 1;
        }
        if (!aUseSeconds) {
            _indexEnd = 2;
        }
        for (int _index = WEEK_INDEX; _index >= _indexEnd; _index--) {
            if ((_timePieces[_index] != 0) || _weFoundTheFirstNonezeroValue) {
                if (aUseCharacters) {
                    if (_timePieces[_index] != 0) {
                        String valueString = longToString(_timePieces[_index], false);
                        if (fixedSize) {
                            for (int i = 0; i < _time[_index].width - valueString.length(); i++) {
                                _result += " ";
                            }
                        }
                        _result += valueString;
                        _result += _time[_index].character;
                        if (_index != _indexEnd) {
                            if (_timePieces[_index - 1] != 0) {
                                _result += _time[_index].seperator;
                            } else if (fixedSize) {
                                _result += " ";

                            }
                        } else {
                            // ---Do not add a separator at the end
                            //                            _result += "_";
                        }
                    } else {
                        if (fixedSize) {
                            for (int i = 0; i < _time[_index].width; i++) {
                                _result += " ";
                            }
                            for (int i = 0; i < _time[_index].character.length(); i++) {
                                _result += " ";
                            }
                            //                        _result += _time[_index].character;
                            if (_index != _indexEnd) {
                                _result += " ";
                            } else {
                                // ---Do not add a separator at the end
                            }
                        }
                    }

                } else {
                    _result += longToString(_timePieces[_index], _weFoundTheFirstNonezeroValue);
                    if (_index != _indexEnd) {
                        _result += _time[_index].seperator;
                    } else {
                        // ---Do not add a seperator at the end
                    }
                }
                _weFoundTheFirstNonezeroValue = true;
            } else {
                // ---Ignore all leading zero values
            }
        }
        // ---In case the result is empty
        if (_result.length() == 0) {
            if (aUseCharacters) {
                if (aUseSeconds) {
                    _result = "0s";
                } else {
                    _result = "0m";
                }
            } else {
                _result = "0";
            }
        } else {
            // ---The result is not ampty
        }
        return prefix + _result;
    }

    public static String longToString(final Long aValue, final boolean aCreateLeadingZero) {
        if (aValue != null) {
            if (!aCreateLeadingZero || (aValue > 9)) {
                return Long.toString(aValue);
            } else {
                return "0" + aValue;
            }
        } else {
            return "";
        }
    }

    static class RcTimeStruct {
        String character;
        String seperator;
        int    width;

        RcTimeStruct(final String aSeperator, final String aCharacter, final int aWidth) {
            seperator = aSeperator;
            character = aCharacter;
            width     = aWidth;
        }
    }

}
