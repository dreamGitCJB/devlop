package com.chyu.www.service;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.chyu.www.Application;

/**
 * @author 蒋侃 E-mail:jiangkan@163.com
 * @version 创建时间：2017年10月24日 上午10:07:32 类说明 redis队列写入数据库
 * 为了保证队列内部的方法有序执行，必须确保一个队列只能被一个进程（线程）调用，为了提高吞吐率，
 * 采用多个队列，每个进程（线程）只获取自己的那个队列中的数据，当队列中的数据执行完毕后，再通过
 * 协调获取一个新的队列进行读取数据。
 * 流程如下：
 * 1、调用者提交任务到【任务队列】，同时说明需要提交到哪个【任务队列】。
 * 2、后台判断该【任务队列】的名称存是否存在于【协调完成队列】中，如果存在，则将该【任务队列】名称添加到一个【协调准备队列】中。
 * 3、后台进程（线程）从【协调准备队列】中获取一个【任务队列】名称。
 * 4、读取该【任务队列】中的数据，并执行任务。
 * 5、执行完该【任务队列】中所有的任务后，将该【任务队列】名称添加到【协调完成队列】
 */
@Service
public class AsyncRedisToDbService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	//协调队列-等待执行，多线程安全
	public static String KEY_ASYNC_READY_LIST = "async_ready_list";
	//协调队列-执行完成
	public static String KEY_ASYNC_FINISH_SET = "async_finish_set";
	// 任务队列前缀，多线程安全
	public static String KEY_ASYNC_TASK_LIST = "async_task_list";

	@Autowired
	RedisTemplate redisTemplate;

	/**
	 * 异步执行操作（写入redis队列)
	 * @param queueName
	 * 			  想要提交到的队列名称，自己随便命名，名称一样就会被放在同一个队列中
	 * @param className
	 *            dao类名
	 * @param method
	 *            dao类的方法名称
	 * @param objects
	 *            方法需要传入的参数
	 */
	public int asyncAddTask(String queueName,String className, String method, Object... args) {
		Object[] objs = new Object[2 + args.length];
		objs[0] = className;
		objs[1] = method;
		for (int i = 0; i < args.length; i++) {
			objs[2 + i] = args[i];
		}
		//将任务放入队列，但并不会执行，除非该队列正在被某个进程（线程）执行中。
		long ret = redisTemplate.opsForList().rightPush(KEY_ASYNC_TASK_LIST+"_"+queueName, objs);
		return ret>0?1:0;
	}
	
	/**
	 * 将队列提交执行，如果同名队列已经完成，则重新提交一个到准备队列中
	 * @param queueName
	 * @return
	 */
	public int asyncSubmit(String queueName){
		//判断同名队列是否已经完成（存在于完成队列中）
		boolean isFinish = redisTemplate.opsForSet().isMember(KEY_ASYNC_FINISH_SET, KEY_ASYNC_TASK_LIST+"_"+queueName);
		//判断准备队列是否为空
		boolean isEmpety = redisTemplate.opsForList().size(KEY_ASYNC_READY_LIST)>0?false:true;
		//如果已经完成或者准备队列为空
		if(isFinish || isEmpety){
			logger.info("原队列已经完成，重新提交一个新的");
			//从完成队列中删除
			redisTemplate.opsForSet().remove(KEY_ASYNC_FINISH_SET, KEY_ASYNC_TASK_LIST+"_"+queueName);
			//将该队列名称重新提交到协调准备队列中，等待执行
			long ret = redisTemplate.opsForList().rightPush(KEY_ASYNC_READY_LIST, KEY_ASYNC_TASK_LIST+"_"+queueName);
			return ret>0?1:0;
		}else{
			return 1;
		}
	}

	/**
	 * 定时执行任务
	 * @Scheduled(cron="0 0/1 * * * ?") //每1分钟执行一次
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	// 每1分钟执行一次
	public void execTasks() {
		//从协调准备队列中获取要执行的任务队列名称
		Object value =redisTemplate.opsForList().leftPop(KEY_ASYNC_READY_LIST);
		if(value==null)
			return;
		//任务队列名称
		String taskListName = (String)value;
		//读取任务队列
		logger.info("开始读取任务队列.......");
		while (redisTemplate.opsForList().size(taskListName) != 0) {
			try {
				logger.info("开始执行异步任务.......");
				value = redisTemplate.opsForList().leftPop(taskListName);
				if (value != null) {
					Object[] objs = (Object[]) value;
					Object bean = Application.ctx.getBean(objs[0].toString());
					Class cls = bean.getClass();
					// 获取方法的参数
					Object[] args = new Object[objs.length - 2];
					for (int i = 0; i < args.length; i++) {
						args[i] = objs[2 + i];
					}
					Method[] methods = cls.getDeclaredMethods();
					for (Method method : methods) {
						if (method.getName().equals(objs[1].toString()) && method.getParameterCount() == args.length) {
							method.invoke(bean, args);
							break;
						}
					}
				}
				logger.info("执行异步任务完成");
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
		//执行完毕后，放入完成队列中
		redisTemplate.opsForSet().add(KEY_ASYNC_FINISH_SET, taskListName);
		logger.info("读取任务队列完成");
	}

}
