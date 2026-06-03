package com.molostream.core.model

/**
 * Kind of media. LIVE has no fixed duration and cannot be resumed; MOVIE/SERIES
 * are on-demand. (SERIES is modelled but unused in the current catalog.)
 */
enum class MediaType { LIVE, MOVIE, SERIES }
