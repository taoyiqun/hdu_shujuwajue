from scipy.io import loadmat
from sklearn import svm
import pandas as pd
import numpy as np
from sklearn.metrics import accuracy_score
from sklearn.model_selection import LeaveOneOut
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
import matplotlib as mpl
from sklearn.metrics import confusion_matrix
from sklearn.metrics import recall_score
import matplotlib.pyplot as plt
def confusion_plt(guess,fact):
    classes = list(set(fact))
    classes.sort()
    confusion = confusion_matrix(guess, fact)
    plt.imshow(confusion, cmap=plt.cm.Blues)
    indices = range(len(confusion))
    plt.xticks(indices, classes)
    plt.yticks(indices, classes)
    plt.colorbar()
    plt.xlabel('guess')
    plt.ylabel('fact')
    for first_index in range(len(confusion)):
        for second_index in range(len(confusion[first_index])):
            plt.text(first_index, second_index, confusion[first_index][second_index])
    plt.show()
#读入数据
m = loadmat("data.mat")
data = pd.DataFrame(m["data"])
label = pd.DataFrame(m["label"])
#预测结果存入label_hat1
label_hat1 = pd.DataFrame(m["label"]).copy()
#留一法训练
loo=LeaveOneOut()
loo.get_n_splits(data)
for train_index,test_index in loo.split(data,label):
    x_train,x_test=data.loc[train_index],data.loc[test_index]
    y_train,y_test=label.loc[train_index],label.loc[test_index]
    #clf = svm.SVC(C=1, kernel='linear', gamma='auto', decision_function_shape='ovr')         #高斯核测试
    clf = svm.SVC(C=1, kernel='linear', decision_function_shape='ovr')
    clf.fit(x_train, y_train.values.ravel())
    label_hat1.loc[test_index] = clf.predict(x_test)
#计算参考基值
count_baseline = accuracy_score(label,label_hat1,False)
accuracy_baseline = accuracy_score(label,label_hat1,True)
print("全部特征进行分类预测成功数量为"+str(count_baseline)+"预测成功率为"+str(accuracy_baseline))
confusion_plt(label_hat1.values.ravel(),label.values.ravel())

#主成分分析进行特征选择
data_t = PCA(n_components=170).fit_transform(data)
#预测结果存入label_hat2
label_hat2 = pd.DataFrame(m["label"]).copy()
#留一法训练
loo=LeaveOneOut()
loo.get_n_splits(data_t)
for train_index,test_index in loo.split(data_t,label):
    x_train,x_test=data_t[train_index],data_t[test_index]
    y_train,y_test=label.loc[train_index],label.loc[test_index]
    clf = svm.SVC(C=1, kernel='linear', decision_function_shape='ovr')
    clf.fit(x_train, y_train.values.ravel())
    label_hat2.loc[test_index] = clf.predict(x_test)
#计算值
count_new = accuracy_score(label,label_hat2,False)
accuracy_new = accuracy_score(label,label_hat2,True)
print("降维后分类预测成功数量为"+str(count_new)+"预测成功率为"+str(accuracy_new))
confusion_plt(label_hat2.values.ravel(),label.values.ravel())
label_new = pd.concat([label,label_hat1,label_hat2],axis=1)
label_new.to_excel('对比.xls')
