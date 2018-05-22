package filters

import com.google.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

class HttpFilter @Inject()(corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter) {
}