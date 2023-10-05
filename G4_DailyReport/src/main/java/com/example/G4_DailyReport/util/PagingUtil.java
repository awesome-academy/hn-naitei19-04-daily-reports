package com.example.G4_DailyReport.util;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.G4_DailyReport.model.User;

public class PagingUtil {
	private final static int PAGE_SIZE = 5;

	public static <T> Page<T> getPage(List<T> objects, int page) {
		int totalElements = objects.size();
		int pageSize = PAGE_SIZE;

		int totalPages = (int) Math.ceil((double) totalElements / pageSize);

		page = Math.max(0, Math.min(page, totalPages - 1));

		int start = page * pageSize;
		int end = Math.min(start + pageSize, totalElements);

		List<T> pageContent = objects.subList(start, end);
		Pageable pageable = PageRequest.of(page, pageSize);

		return new PageImpl<>(pageContent, pageable, totalElements);
	}

	public static int calculatePageNumber(List<User> userList, UUID userId) {

		int pageSize = PAGE_SIZE;
		int userIndex = -1;
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getId().equals(userId)) {
				userIndex = i;
				System.out.println(i);
				break;
			}
		}
		if (userIndex >= 0) {
			return userIndex / pageSize;
		}
		    return 0;
	}
}
