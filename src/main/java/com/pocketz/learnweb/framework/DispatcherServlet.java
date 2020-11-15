package com.pocketz.learnweb.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketz.learnweb.controller.IndexController;
import com.pocketz.learnweb.controller.UserController;

@WebServlet(urlPatterns = "/")
public class DispatcherServlet extends HttpServlet {
	private static final Set<Class<?>> supportedGetParameterTypes = Set.of(int.class, long.class, boolean.class,
			String.class, HttpServletRequest.class, HttpServletResponse.class, HttpSession.class);
	private static final Set<Class<?>> supportedPostParameterTypes = Set.of(HttpServletRequest.class,
			HttpServletResponse.class, HttpSession.class);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, GetDispatcher> getMappings = new HashMap<>();
	private Map<String, PostDispatcher> postMappings = new HashMap<>();

	private ViewEngine viewEngine;

	private List<Class<?>> controllers = List.of(IndexController.class, UserController.class);

	@Override
	public void init() throws ServletException {
		scanInController(RequestType.GET);
		scanInController(RequestType.POST);
		this.viewEngine = new ViewEngine(getServletContext());
		for (String key: getMappings.keySet()) {
			System.out.println(key);
			
		}
		for (String key: postMappings.keySet()) {
			System.out.println(key);
			
		}
	}

	private void scanInController(RequestType type) throws ServletException {
//		 enum
//		switch (type) {
//		case GET:
//
//			break;
//		case POST:
//
//			break;
//		default:
//			break;
//		}
		logger.info("init {}... for {}", getClass().getSimpleName(), type);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		for (Class<?> controllerClass : controllers) {
			try {
				Object controllerInstance = controllerClass.getConstructor().newInstance();
				for (Method method : controllerClass.getMethods()) {
					boolean annotationNotNull = false;
					boolean parameterNotSpt = false;
					switch (type) {
					case GET:
						annotationNotNull = (method.getAnnotation(GetMapping.class) != null);
						break;
					case POST:
						annotationNotNull = (method.getAnnotation(PostMapping.class) != null);
						break;
					default:
						annotationNotNull = false;
						break;
					}
					// annotation
					if (annotationNotNull) {
						if (method.getReturnType() != ModelAndView.class && method.getReturnType() != void.class) {
							throw new UnsupportedOperationException(
									"Unsupported return type:" + method.getReturnType() + "for method:" + method);
						}
						Class<?> requestBodyClass = null;
						for (Class<?> parameterClass : method.getParameterTypes()) {
							switch (type) {
							case GET:
								parameterNotSpt = !supportedGetParameterTypes.contains(parameterClass);
								break;
							case POST:
								parameterNotSpt = !supportedPostParameterTypes.contains(parameterClass);
								break;
							default:
								parameterNotSpt = false;
								break;
							}
							if (parameterNotSpt) {
								switch (type) {
								case GET:
									throw new UnsupportedOperationException(
											"Unsupported parameter type: " + parameterClass + " for method: " + method);
//										break;
								case POST:
									if (requestBodyClass == null) {
										// post
									} else {
										throw new UnsupportedOperationException(
												"Unsupported duplicate request body type: " + parameterClass
														+ " for method: " + method);
									}
									break;
								default:
									break;
								}
							}
						}
						String path = null;
						switch (type) {
						case GET:				
							String[] parameterNames = Arrays.stream(method.getParameters()).map(p -> p.getName())
							.toArray(String[]::new);
							
							path = method.getAnnotation(GetMapping.class).value();
							logger.info("Found GET: {} => {}", path, method);
							this.getMappings.put(path, new GetDispatcher(controllerInstance, method, parameterNames,
									method.getParameterTypes()));
							break;
						case POST:				
							path = method.getAnnotation(PostMapping.class).value();
							logger.info("Found POST: {} => {}", path, method);
							this.postMappings.put(path, new PostDispatcher(controllerInstance, method,
									method.getParameterTypes(), objectMapper));
							break;
						default:
							break;
						}
					} else {
//							logger.error("Annotation is null in {}",method.getName());
					}

				}
			} catch (ReflectiveOperationException e) {
				// TODO: handle exception
				throw new ServletException(e);
			}
		}
		
	}

//	@Override
//	public void init() throws ServletException {
//		logger.info("init {}...", getClass().getSimpleName());
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		// 依次处理每个Controller:
//		for (Class<?> controllerClass : controllers) {
//			try {
//				Object controllerInstance = controllerClass.getConstructor().newInstance();
//				// 依次处理每个Method:
//				for (Method method : controllerClass.getMethods()) {
//					if (method.getAnnotation(GetMapping.class) != null) {
//						// 处理@Get:
//						if (method.getReturnType() != ModelAndView.class && method.getReturnType() != void.class) {
//							throw new UnsupportedOperationException(
//									"Unsupported return type: " + method.getReturnType() + " for method: " + method);
//						}
//						for (Class<?> parameterClass : method.getParameterTypes()) {
//							if (!supportedGetParameterTypes.contains(parameterClass)) {
//								throw new UnsupportedOperationException(
//										"Unsupported parameter type: " + parameterClass + " for method: " + method);
//							}
//						}
//						String[] parameterNames = Arrays.stream(method.getParameters()).map(p -> p.getName())
//								.toArray(String[]::new);
//						String path = method.getAnnotation(GetMapping.class).value();
//						logger.info("Found GET: {} => {}", path, method);
//						this.getMappings.put(path, new GetDispatcher(controllerInstance, method, parameterNames,
//								method.getParameterTypes()));
//					} else if (method.getAnnotation(PostMapping.class) != null) {
//						// 处理@Post:
//						if (method.getReturnType() != ModelAndView.class && method.getReturnType() != void.class) {
//							throw new UnsupportedOperationException(
//									"Unsupported return type: " + method.getReturnType() + " for method: " + method);
//						}
//						Class<?> requestBodyClass = null;
//						for (Class<?> parameterClass : method.getParameterTypes()) {
//							if (!supportedPostParameterTypes.contains(parameterClass)) {
//								if (requestBodyClass == null) {
//									requestBodyClass = parameterClass;
//								} else {
//									throw new UnsupportedOperationException("Unsupported duplicate request body type: "
//											+ parameterClass + " for method: " + method);
//								}
//							}
//						}
//						String path = method.getAnnotation(PostMapping.class).value();
//						logger.info("Found POST: {} => {}", path, method);
//						this.postMappings.put(path, new PostDispatcher(controllerInstance, method,
//								method.getParameterTypes(), objectMapper));
//					}
//				}
//			} catch (ReflectiveOperationException e) {
//				throw new ServletException(e);
//			}
//		}
//		// 创建ViewEngine:
//		this.viewEngine = new ViewEngine(getServletContext());
//	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, this.getMappings);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, this.postMappings);
	}

	protected void process(HttpServletRequest req, HttpServletResponse resp,
			Map<String, ? extends AbstractDispatcher> dispatcherMap) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		logger.info(req.getRequestURI());
		String path = req.getRequestURI().substring(req.getContextPath().length());
		AbstractDispatcher dispatcher = dispatcherMap.get(path);
		System.out.println(dispatcher);
		if (dispatcher == null) {
			resp.sendError(404);
			return;
		}
		ModelAndView mv = null;
		try {
			mv = dispatcher.invoke(req, resp);
		} catch (ReflectiveOperationException e) {
			throw new ServletException(e);
		}
		if (mv == null) {
			return;
		}
		if (mv.view.startsWith("redirect:")) {
			resp.sendRedirect(mv.view.substring(9));
			return;
		}
		PrintWriter pw = resp.getWriter();
		this.viewEngine.render(mv, pw);
		pw.flush();
	}

}
