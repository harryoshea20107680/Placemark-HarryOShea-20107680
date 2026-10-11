
package org.wit.placemark

import org.wit.placemark.models.PlacedMark

interface OnMarkListener {

    fun onMarkClick(mark: PlacedMark)

    fun onMarkLongClick(mark: PlacedMark): Boolean
}
