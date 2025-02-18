package vn.com.unit.springframework.data.mirage.repository.support;

import java.lang.reflect.Method;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AnnotationRepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

public class MirageDefaultRepositoryMetadata extends DefaultRepositoryMetadata {

	public MirageDefaultRepositoryMetadata(Class<?> repositoryInterface) {
		super(repositoryInterface);
	}
	@Override
	public Class<?> getReturnedDomainClass(Method method) {

		TypeInformation<?> returnType = getReturnType(method);
		returnType = ReactiveWrapperConverters.unwrapWrapperTypes(returnType);

		return MirageQueryExecutionConverters.unwrapWrapperTypes(returnType, getDomainTypeInformation()).getType();
	}
	
	public static RepositoryMetadata getMetadata(Class<?> repositoryInterface) {

		Assert.notNull(repositoryInterface, "Repository interface must not be null");

		return Repository.class.isAssignableFrom(repositoryInterface) ? new MirageDefaultRepositoryMetadata(repositoryInterface)
				: new AnnotationRepositoryMetadata(repositoryInterface);
	}
}
