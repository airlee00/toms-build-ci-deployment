repo_path = ARGV[0]
transaction = ARGV[1]
mysql='D:\BitNami\redmine-2.3.2-1\mysql\bin\mysql.exe'
commit_log=`svnlook log #{repo_path} -t #{transaction}`

if(commit_log == nil || commit_log.length < 2 )
	STDERR.puts("Log message cannot be empty");
	exit(1)
end

if(commit_log =~ /IssueNumber\s*#([A-Za-z0-9_-]+)\s*/)
	issue_number = $1
	#redmine_issue_open =`#{mysql} --database=bitnami_redmine --user=bitnami --password=832107c1ad -e "SELECT COUNT(*) FROM issues I INNER JOIN issue_statuses S ON S.id = I.status_id WHERE S.is_closed = 0 AND I.id = #{issue_number};" --skip-column-names`.strip()
	redmine_issue_open =`#{mysql} --database=test --user=root --password=123qwe -e "select count(*) from spaces a,items b where a.id=b.id and substr('#{issue_number}',1,3)=a.prefix_code and substr('#{issue_number}',5,1)=b.id;" --skip-column-names`.strip()
	if(redmine_issue_open.eql?("0"))
		STDERR.puts("Redmine issue #{issue_number} is not in an open state.")
		exit(1)
    end
else
	STDERR.puts("Redmine Issue number is required")
	exit(1)
end
