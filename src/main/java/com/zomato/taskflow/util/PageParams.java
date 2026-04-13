package com.zomato.taskflow.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PageParams {

	public static final int DEFAULT_LIMIT = 20;
	public static final int MAX_LIMIT = 100;

	private PageParams() {
	}

	public static int pageOrDefault(Integer page) {
		if (page == null || page < 1) {
			return 1;
		}
		return page;
	}

	public static int limitOrDefault(Integer limit) {
		if (limit == null || limit < 1) {
			return DEFAULT_LIMIT;
		}
		return Math.min(limit, MAX_LIMIT);
	}

	public static PageRequest toPageRequest(int pageOneBased, int limit, Sort sort) {
		return PageRequest.of(pageOneBased - 1, limit, sort);
	}
}
