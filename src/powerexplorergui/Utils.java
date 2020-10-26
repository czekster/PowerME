/*
 * Copyright (C) 2020 Ricardo M. Czekster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package powerexplorergui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Ricardo M. Czekster
 */
public class Utils {

    /**
     * Method getTime()
     * @return Returns current time in HH:mm:ss formatted as a String 
     */
    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        return ("[" + new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()) + "] ");
    }

    /**
     * Method getFileExtension(String)
     * @return Returns the extension of a file, e.g., model.xml it returns xml
     */
    public static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
