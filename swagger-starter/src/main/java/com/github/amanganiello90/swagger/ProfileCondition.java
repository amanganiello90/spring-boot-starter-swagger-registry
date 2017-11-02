package com.github.amanganiello90.swagger;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;

import org.springframework.core.type.AnnotatedTypeMetadata;

public class ProfileCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		// TODO Auto-generated method stub

		String activeProfile = "swagger-ui";

		String profiles[] = context.getEnvironment().getActiveProfiles();

		for (String profile : profiles) {
			if (profile.equals(activeProfile)) {
				return true;
			}
		}

		return false;
	}

}
