package com.toms.scm.ci.io;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */

public enum JobStatus {

        OK("blue"),
        WARNING("yellow"),
        FAILED("red"),
        INPROGRESS("anime"),
        PENDING("grey"),
        NOTBUILT("notbuilt")
        ;
        private String value;

		public String getValue() {
			return value;
		}

		private JobStatus(String value) {
			this.value = value;
		}
        
		public static JobStatus create(String value) {
			if(value.endsWith("anime"))
				return JobStatus.INPROGRESS;
			else{
				for (JobStatus e : JobStatus.values()) {
					if (e.getValue().equals(value)) {
						return e;
					}
				}
				return null;
			}
		}
        
}
