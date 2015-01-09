#define ___PASTE(a,b) a##b
#define __PASTE(a,b) ___PASTE(a,b)
#define UNIQUE(x) __PASTE(x,__COUNTER__)

echo __COUNTER__;
echo __COUNTER__;

echo UNIQUE(a);
echo UNIQUE(a);